import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.StringTokenizer;

public class prefixer {
	public static void main(String args[]) {
		String infixString, prefixString = null;

		// command line argument check
		if (args.length != 1) {
			System.err.println("exactly one argument required (the filename)");
			System.exit(1);
		}

		// read expression from file
		infixString = readFile(args);
		// convert infix to prefix
		prefixString = infixToPrefix(infixString);

		System.out.println(prefixString);
	}

	/**
	 * algorithm to convert an infix expression to a prefix version
	 * 
	 * the trick is to reverse the input string and use two stacks, one stack
	 * for the output and one for holding operators.
	 */
	private static String infixToPrefix(String expression) {
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
				// pop operators from stack until a ")" is encountered
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

		// push remaining operators into output
		while (!stack.empty()) {
			output.push(stack.pop());
		}

		return buildOutputString(output);
	}

	/**
	 * constructs the prefix expression
	 * 
	 * reverses the output Stack from infixToPrefix, meanwhile adding "(" and
	 * ")" when needed
	 */
	private static String buildOutputString(Stack<String> output) {
		StringBuffer toReturn = new StringBuffer();

		// to mark when we found an operator so we can prepend a "("
		boolean foundOperator = false;
		// used in conjunction with foundOperator to count two operands before
		// appending a ")"
		int operatorCount = 0;
		// used to determine if we need a ")" at the end of the entire
		// expression
		int leftParenCount = 0;
		int rightParenCount = 0;

		while (!output.empty()) {
			String op = output.pop();

			// if the next output is an operator, prepend a "("
			if (isOperator(op)) {
				toReturn.append("(");
				// if a previous operand was already found, but operatorCount
				// hasn't reached 2, then reset the counter for the ")"
				if (foundOperator) {
					operatorCount = 0;
				} else {
					foundOperator = true;
				}
				leftParenCount++;
			} else if (foundOperator) {
				operatorCount++;
			}

			toReturn.append(op);

			// once we hit two consecutive operands, append a ")" to close
			if (operatorCount == 2) {
				toReturn.append(")");
				rightParenCount++;
				operatorCount = 0;
			}

			if (!output.empty()) {
				toReturn.append(" ");
			} else if (rightParenCount < leftParenCount) {
				toReturn.append(")");
			}
		}

		return toReturn.toString();
	}

	// TRUE if a is lower priority than b, FALSE if a is higher priority than b
	private static boolean isLowerPri(String a, String b) {
		return "+*-/".indexOf(a.charAt(0)) % 2 < "+*-/".indexOf(b.charAt(0)) % 2;
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
