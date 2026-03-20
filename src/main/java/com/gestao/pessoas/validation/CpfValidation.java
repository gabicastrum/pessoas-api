package com.gestao.pessoas.validation;

public class CpfValidation {

    private CpfValidation() {}

    public static void validar(String cpf) {
        if (cpf == null) throw new IllegalArgumentException("CPF inválido");

        String numeros = cpf.replaceAll("[^\\d]", "");

        if (numeros.length() != 11 || numeros.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido");
        }

        int soma = 0;
        for (int i = 0; i < 9; i++)
            soma += (numeros.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (soma % 11);
        if (d1 >= 10) d1 = 0;

        soma = 0;
        for (int i = 0; i < 10; i++)
            soma += (numeros.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (soma % 11);
        if (d2 >= 10) d2 = 0;

        if ((numeros.charAt(9) - '0') != d1 || (numeros.charAt(10) - '0') != d2) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }
}