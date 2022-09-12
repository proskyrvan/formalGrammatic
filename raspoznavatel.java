import java.util.*;

public class PrecedenceGrammar {

    public static void main(String[] args) {
        System.out.print("Enter string: ");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        System.out.println(isValid(line + "#"));
    }
    
    // Создаём грамматику
    private static final Map<Character, List<String>> grammar = new HashMap<>(){{
        put('S', List.of("aA", "caB", "ca"));
        put('A', List.of("bA", "cbS", "b"));
        put('B', List.of("CB", "C"));
        put('C', List.of("Dx"));
        put('D', List.of("x"));
    }};
    // Создаем матрицу предшествования
    private static final Integer[][] matrix = new Integer[][]{
          //  S     A      B    C      D    a      c    b     x     #
            {null, null, null, null, null, null, null, null, null,   1 }, // S
            {null, null, null, null, null, null, null, null, null,   1 }, // A
            {null, null, null, null, null, null, null, null, null,   1 }, // B
            {null, null,  0  ,  -1 ,  -1 , null, null, null,  -1 ,   1 }, // C
            {null, null, null, null, null, null, null, null,  0  ,   1 }, // D
            {null,  0  ,  0  ,  -1 ,  -1 , null, -1  , -1  , -1  ,   1 }, // a
            {null, null, null, null, null,  0  , null,  0  , null,   1 }, // c
            { 0,    0  , null, null, null,  -1 ,  -1 ,  -1 , null,   1 }, // b
            {null, null,  1  ,  1  ,  1  , null, null, null,  1  ,   1 }, // x
            { -1 ,  -1 ,  -1 ,  -1 ,  -1 ,  -1 ,  -1 ,  -1 ,  -1 , null}, // #
    };
    // Для удобства поиска по матрице предшествования
    private static final Map<Character, Integer> indices = new HashMap<>() {{
        put('S', 0);
        put('A', 1);
        put('B', 2);
        put('C', 3);
        put('D', 4);
        put('a', 5);
        put('c', 6);
        put('b', 7);
        put('x', 8);
        put('#', 9);
    }};

    private static Integer getValueFromMatrix(char left, char right) {
        return matrix[indices.get(left)][indices.get(right)];
    }

    private static boolean isValid(String line) {
        char[] arr = line.toCharArray();
        int pos = 0;

        Stack<Character> stack = new Stack<>();
        stack.add('#');
        boolean result = false;

        if (arr.length == 1) {
            return false;
        }
        while (pos <= arr.length) {
            char c = arr[pos];
            if (!indices.containsKey(c)) {
                result = true;
                break;
            }
            if (c == '#' && stack.size() == 2 && stack.peek() == 'S')
                break;
            Integer relation = getValueFromMatrix(stack.peek(), arr[pos]);
            if (relation == null) {
                result = true;
                break;
            }
            if (relation <= 0) {
                stack.push(c);
                pos++;
            } else {
                StringBuffer buffer = new StringBuffer();
                Iterator<Map.Entry<Character, List<String>>> iterator = grammar.entrySet().iterator();
                boolean found = false;
                char leftPart = 0;
                char lastChar;
                do {
                    lastChar = stack.pop();
                    buffer.insert(0, lastChar);
                } while (getValueFromMatrix(stack.peek(), lastChar) >= 0);

                while (iterator.hasNext() && !found) {
                    Map.Entry<Character, List<String>> entry = iterator.next();
                    if (entry.getValue().contains(buffer.toString())) {
                        found = true;
                        leftPart = entry.getKey();
                    }
                }
                if (!found) {
                    result = true;
                    break;
                }
                stack.add(leftPart);
            }
        }
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.insert(0, stack.pop());
            buffer.insert(0, stack.pop());
            return !result && buffer.toString().equals("#S");
        } catch (EmptyStackException e) {
            return false;
        }
    }
}
