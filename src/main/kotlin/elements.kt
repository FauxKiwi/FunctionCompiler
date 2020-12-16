abstract class Token
enum class TokenType { EXPR_START, EXPR_END, EXPR_REP, NUMBER, OPERATOR, SIGN }
class ExprStartToken : Token()
class ExprEndToken : Token()
class ExpressionRepresentToken(val expr: List<Token>) : Token()
class NumberToken(var number: Int) : Token()
class OperatorToken(val operator: Operator) : Token()
class SignToken(val sign: Boolean /* + = true; - = false */) : Token()
enum class Operator(rank: Int) {
    PLUS(0), MINUS(0), TIMES(1), DIV(1), REM(1), POW(2);
    companion object {
        fun ofChar(c: Char) = when (c) {
            '+' -> PLUS; '-' -> MINUS; '*' -> TIMES; '/' -> DIV; '%' -> REM; '^' -> POW; else -> throw SyntaxError("Unknown operator token '$c'")
        }
    }
    override fun toString(): String = when (this) {
        PLUS -> "+"; MINUS -> "-"; TIMES -> "*"; DIV -> "/"; REM -> "%"; POW -> "^"
    }
}

abstract class TreeNode(val parent: TreeNode?)
class ExpressionNode(parent: TreeNode?, var rootNode: TreeNode?) : TreeNode(parent)
class NumberNode(parent: TreeNode?, val number: Int) : TreeNode(parent)
class OperatorNode(parent: TreeNode?, val operator: Operator, var leftChild: TreeNode?, var rightChild: TreeNode?) :
    TreeNode(parent)

class SyntaxError(description: String?) : Error(description)
class MathematicalError(description: String?): Error(description)