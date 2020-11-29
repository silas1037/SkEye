package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MathIllegalStateException extends IllegalStateException implements ExceptionContextProvider {
    private static final long serialVersionUID = -6024911025449780478L;
    private final ExceptionContext context;

    public MathIllegalStateException(Localizable pattern, Object... args) {
        this.context = new ExceptionContext(this);
        this.context.addMessage(pattern, args);
    }

    public MathIllegalStateException(Throwable cause, Localizable pattern, Object... args) {
        super(cause);
        this.context = new ExceptionContext(this);
        this.context.addMessage(pattern, args);
    }

    public MathIllegalStateException() {
        this(LocalizedFormats.ILLEGAL_STATE, new Object[0]);
    }

    @Override // org.apache.commons.math3.exception.util.ExceptionContextProvider
    public ExceptionContext getContext() {
        return this.context;
    }

    public String getMessage() {
        return this.context.getMessage();
    }

    public String getLocalizedMessage() {
        return this.context.getLocalizedMessage();
    }
}
