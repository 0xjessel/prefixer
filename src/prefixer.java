import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

public class prefixer {
	public static void main(String args[]) {
		String infixString, prefixString = null;

		// command line argument check
		if (args.length != 1) {
			System.err.println("exactly one argument required");
			System.exit(1);
		}

		// read expression from file
		infixString = readFile(args);
		// convert infix to prefix
		prefixString = infixToPrefix(infixString);

		System.out.println(prefixString);
	}

	private static String infixToPrefix(String expression) {
		StringBuffer toReturn = new StringBuffer();
		StringBuffer reversed = new StringBuffer(expression).reverse();
		StringTokenizer tokens = new StringTokenizer(reversed.toString());
		Stack<String> stack = new Stack<String>();
		Stack<String> output = new Stack<String>();

		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();

			if (isOperand(token)) {
				output.push(token);
			} else if (token.equals(")")) {
				stack.push(token);
			} else if (isOperator(token)) {
				boolean done = true;
				do {
					if (stack.empty()) {
						stack.push(token);
						done = true;
					} else if (stack.peek().equals(")")) {
						stack.push(token);
						done = true;
					} else if (!isLowerPri(token, stack.peek())) {
						stack.push(token);
						done = true;
					} else {
						output.push(stack.pop());
						done = false;
					}
				} while (done == false);
			} else if (token.equals("(")) {
				while (!stack.empty()) {
					String operator = stack.pop();
					if (!operator.equals(")")) {
						output.push(operator);
					} else {
						break;
					}
				}
			}
		}

		while (!stack.empty()) {
			output.push(stack.pop());
		}
		
		while (!output.empty()) {
			toReturn.append(output.pop());
			toReturn.append(" ");
		}
		return toReturn.toString();
	}

	// true is a is lower pri than b, false if a is higher pri than b
	private static boolean isLowerPri(String a, String b) {
		return "+*-/".indexOf(a.charAt(0))%2 < "+*-/".indexOf(b.charAt(0))%2;
	}

	private static boolean isOperand(String token) {
		Character c = token.charAt(0);
		return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'));
	}

	private static boolean isOperator(String token) {
		return "+-*/".indexOf(token) != -1;
	}

	private static String readFile(String[] args) {
		try {
			FileInputStream fstream = new FileInputStream(args[0]);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strline;
			if ((strline = br.readLine()) != null) {
				in.close();
				return strline;
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return null;
	}
}
