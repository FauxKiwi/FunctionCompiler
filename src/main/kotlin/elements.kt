import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

abstract class Token
enum class TokenType { EXPR_START, EXPR_END, EXPR_REP, FUN_REP, NUMBER, VAR, OPERATOR, SIGN }
class ExprStartToken : Token()
class ExprEndToken : Token()
class ExpressionToken(val expr: List<Token>) : Token()
class FunctionToken(val function: Function, val expr: List<Token>) : Token()
class NumberToken(var number: Double) : Token()
class DotToken : Token()
class VarToken(val name: String) : Token()
class OperatorToken(val operator: Operator) : Token()
class SignToken(val sign: Boolean /* + : true; - : false */) : Token()
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
open class Function(val name: String, val f: (Double) -> Double) {
    object Sinus : Function("sin", ::sin)
    object Cosinus : Function("cos", ::cos)
    object Tangens : Function("tan", ::tan)
    object Sqrt : Function("sqrt", ::sqrt)
    companion object {
        private val pool = hashMapOf(Pair("sin", Sinus), Pair("cos", Cosinus), Pair("tan", Tangens), Pair("sqrt", Sqrt))
        fun ofString(name: String) = pool[name] ?: throw SyntaxError("Unknown function \"$name\"")
    }
    operator fun invoke(x: Double): Double = f(x)
}

abstract class TreeNode(val parent: TreeNode?)
class ExpressionNode(parent: TreeNode?, var rootNode: TreeNode?) : TreeNode(parent)
class FunctionNode(parent: TreeNode?, val function: Function, var expr: TreeNode?) : TreeNode(parent)
class NumberNode(parent: TreeNode?, val number: Double) : TreeNode(parent)
class VarNode(parent: TreeNode?, val name: String) : TreeNode(parent)
class OperatorNode(parent: TreeNode?, val operator: Operator, var leftChild: TreeNode?, var rightChild: TreeNode?) :
    TreeNode(parent)

class SyntaxError(description: String?) : Error(description)
class MathematicalError(description: String?): Error(description)