package com.gestao.pessoas.exception;

public class CpfExisteException extends RuntimeException {
    public CpfExisteException() {
        super("CPF já cadastrado");
    }
}
