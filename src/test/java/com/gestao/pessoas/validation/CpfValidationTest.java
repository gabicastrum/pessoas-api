package com.gestao.pessoas.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CpfValidation")
class CpfValidationTest {

    private static final String CPF_VALIDO = "111.444.777-35";
    private static final String CPF_VALIDO_SEM_MASCARA = "11144477735";
    private static final String CPF_SEQUENCIA_REPETIDA = "111.111.111-11";
    private static final String CPF_DIGITO_INVALIDO = "111.444.777-00";
    private static final String CPF_CURTO = "111.444.777";
    private static final String CPF_NULO = null;

    @Nested
    @DisplayName("CPFs válidos")
    class CpfsValidos {

        @Test
        @DisplayName("Deve aceitar CPF válido com máscara")
        void deveAceitarCpfComMascara() {
            assertDoesNotThrow(() -> CpfValidation.validar(CPF_VALIDO));
        }

        @Test
        @DisplayName("Deve aceitar CPF válido sem máscara")
        void deveAceitarCpfSemMascara() {
            assertDoesNotThrow(() -> CpfValidation.validar(CPF_VALIDO_SEM_MASCARA));
        }
    }

    @Nested
    @DisplayName("CPFs inválidos")
    class CpfsInvalidos {

        @Test
        @DisplayName("Deve rejeitar CPF com sequência repetida")
        void deveRejeitarSequenciaRepetida() {
            assertThrows(IllegalArgumentException.class,
                    () -> CpfValidation.validar(CPF_SEQUENCIA_REPETIDA));
        }

        @Test
        @DisplayName("Deve rejeitar CPF com dígito verificador errado")
        void deveRejeitarDigitoVerificadorErrado() {
            assertThrows(IllegalArgumentException.class,
                    () -> CpfValidation.validar(CPF_DIGITO_INVALIDO));
        }

        @Test
        @DisplayName("Deve rejeitar CPF com menos de 11 dígitos")
        void deveRejeitarCpfCurto() {
            assertThrows(IllegalArgumentException.class,
                    () -> CpfValidation.validar(CPF_CURTO));
        }

        @Test
        @DisplayName("Deve rejeitar CPF nulo")
        void deveRejeitarCpfNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> CpfValidation.validar(CPF_NULO));
        }
    }
}