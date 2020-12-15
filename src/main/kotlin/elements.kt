abstract class Token(val type: TokenType)
enum class TokenType { EXPRESSION, NUMBER, OPERATOR, SIGN }
class ExpressionToken(val start/* | end*/: Boolean) : Token(TokenType.EXPRESSION)
class NumberToken(var number: Int) : Token(TokenType.NUMBER)
class OperatorToken(val operator: Operator) : Token(TokenType.OPERATOR)
class SignToken(val sign: Boolean /* + = true; - = false */) : Token(TokenType.SIGN)
enum class Operator(rank: Int) {
    PLUS(0), MINUS(0), TIMES(1), DIV(1), REM(1), POW(2);

    companion object {
        fun ofChar(c: Char) = when (c) {
            '+' -> PLUS; '-' -> MINUS; '*' -> TIMES; '/' -> DIV; '%' -> REM; '^' -> POW; else -> throw SyntaxError("Unknown operator token '$c'")
        }
    }
}

abstract class TreeNode(val parent: TreeNode?)
class ExpressionNode(parent: TreeNode?, var rootNode: TreeNode?) : TreeNode(parent)
class NumberNode(parent: TreeNode?, val number: Int) : TreeNode(parent)
class OperatorNode(parent: TreeNode?, val operator: Operator, var leftChild: TreeNode?, var rightChild: TreeNode?) :
    TreeNode(parent)

class SyntaxError(description: String?) : Error(description)
class MathematicalError(description: String?): Error(description)