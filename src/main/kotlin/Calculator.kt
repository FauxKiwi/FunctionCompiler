object Calculator {
    operator fun invoke(function: TreeNode): Int {
        try {
            return when (function) {
                is ExpressionNode -> Calculator(function.rootNode!!)
                is OperatorNode -> when (function.operator) {
                    Operator.PLUS -> Calculator(function.leftChild!!) + Calculator(function.rightChild!!)
                    Operator.MINUS -> Calculator(function.leftChild!!) - Calculator(function.rightChild!!)
                    Operator.TIMES -> Calculator(function.leftChild!!) * Calculator(function.rightChild!!)
                    Operator.DIV -> Calculator(function.leftChild!!) / Calculator(function.rightChild!!)
                    Operator.REM -> Calculator(function.leftChild!!) % Calculator(function.rightChild!!)
                    Operator.POW -> Calculator(function.leftChild!!) pow Calculator(function.rightChild!!)
                }
                is NumberNode -> function.number
                else -> 0
            }
        } catch (e: NullPointerException) {
            throw SyntaxError("Incomplete tree node")
        } catch (e: ArithmeticException) {
            throw MathematicalError(e.message)
        }
    }

    infix fun Int.pow(x: Int): Int {
        return if (x == 0) 1 else if (x > 0) this.times(pow(x - 1)) else 0
    }
}