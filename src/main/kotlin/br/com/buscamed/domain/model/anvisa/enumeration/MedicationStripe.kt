package br.com.buscamed.domain.model.anvisa.enumeration

/**
 * Representa a classificação de venda do medicamento (Tarja).
 */
enum class MedicationStripe(val description: String) {
    RED("Tarja Vermelha"),
    BLACK("Tarja Preta"),
    OVER_THE_COUNTER("Venda livre"),
    UNSTRIPED("Sem Tarja");

    companion object {
        /**
         * Resolve a tarja do medicamento com base na string fornecida no CSV.
         *
         * @param value O texto extraído da coluna do CSV.
         * @return A [MedicationStripe] correspondente ou [UNSTRIPED] caso seja o valor nulo/default do CSV.
         */
        fun fromDescription(value: String?): MedicationStripe {
            if (value.isNullOrBlank() || value.contains("- (*)")) return UNSTRIPED
            return entries.find { it.description.equals(value.trim(), ignoreCase = true) } ?: UNSTRIPED
        }
    }
}