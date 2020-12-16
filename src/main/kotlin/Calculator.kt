object Calculator {
    operator fun invoke(function: String, vararg vars: Pair<String, Double>): Double = Calculator(Parser(Lexer(function)), *vars)

    operator fun invoke(function: TreeNode, vararg vars: Pair<String, Double>): Double {
        val varsMap = hashMapOf(*vars)
        try {
            return when (function) {
                is RootNode -> Calculator(function.node!!, *vars)
                is ExpressionNode -> Calculator(function.rootNode!!, *vars)
                is OperatorNode -> when (function.operator) {
                    Operator.PLUS -> Calculator(function.leftChild!!, *vars) + Calculator(function.rightChild!!, *vars)
                    Operator.MINUS -> Calculator(function.leftChild!!, *vars) - Calculator(function.rightChild!!, *vars)
                    Operator.TIMES -> Calculator(function.leftChild!!, *vars) * Calculator(function.rightChild!!, *vars)
                    Operator.DIV -> Calculator(function.leftChild!!, *vars) / Calculator(function.rightChild!!, *vars)
                    Operator.REM -> Calculator(function.leftChild!!, *vars) % Calculator(function.rightChild!!, *vars)
                    Operator.POW -> Calculator(function.leftChild!!, *vars) pow Calculator(function.rightChild!!, *vars).toInt()
                }
                is NumberNode -> function.number
                is VarNode -> varsMap[function.name] ?: throw SyntaxError("No value provided for variable \"${function.name}\"")
                else -> 0.0
            }
        } catch (e: NullPointerException) {
            throw SyntaxError("Incomplete tree node")
        } catch (e: ArithmeticException) {
            throw MathematicalError(e.message)
        }
    }

    infix fun Double.pow(x: Int): Double {
        return if (x == 0) 1.0 else if (x > 0) this.times(pow(x - 1)) else 0.0
    }
}