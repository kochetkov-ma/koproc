package ru.iopump.koproc

data class KoprocResult(
    val call: KprocCall,
    val code: Int,
    val out: String,
    val errorOut: String,
    val pid: Long,
    val error: Throwable? = null
) {

    /**
     * [Throwable] during execution.
     */
    val hasError = error != null

    /**
     * No [Throwable] during execution.
     */
    val hasNotError = hasError.not()

    /**
     * Exit code = 0.
     */
    val hasSuccessCode = code == 0

    /**
     * Exit code != 0.
     */
    val hasNotSuccessCode = hasSuccessCode.not()

    /**
     * Exit code == 0 and no [Throwable] during execution.
     * [hasNotError] && [hasSuccessCode]
     */
    val isSuccess = hasNotError && hasSuccessCode

    /**
     * Exit code != 0 or any [Throwable] during execution.
     * not [isSuccess]
     */
    val isNotSuccess = isSuccess.not()

    /**
     * [RuntimeException] if [hasError].
     *
     * @throws RuntimeException
     */
    fun throwOnError(msg: String = "Command execution result has error") = apply {
        if (hasError) throw RuntimeException("$msg. ${toString()}", error)
    }

    /**
     * [RuntimeException] if [hasNotSuccessCode].
     *
     * @throws RuntimeException
     */
    fun throwOnUnSuccessCode(msg: String = "Command execution exit code is not zero") = apply {
        if (hasNotSuccessCode) throw RuntimeException("$msg. ${toString()}", error)
    }

    /**
     * [RuntimeException] if [isNotSuccess].
     *
     * @throws RuntimeException
     */
    fun throwOnAnyFailure(msg: String = "Command execution failed") = apply {
        if (isNotSuccess) throw RuntimeException("$msg. ${toString()}", error)
    }

    override fun toString(): String {
        return "[code=$code] [pid=$pid] '${call.command}'\nout='$out'\nerrorOut='$errorOut'\nerror=$error"
    }
}