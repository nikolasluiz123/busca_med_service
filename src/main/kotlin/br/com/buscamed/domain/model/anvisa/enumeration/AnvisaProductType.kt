package br.com.buscamed.domain.model.anvisa.enumeration

/**
 * Representa as categorias de produtos definidos pela ANVISA.
 */
enum class AnvisaProductType(val description: String) {
    BIOLOGIC("Biológico"),
    NEW_BIOLOGIC("Biológico Novo"),
    SIMILAR("Similar"),
    GENERIC("Genérico"),
    NEW("Novo"),
    SPECIFIC("Específico"),
    RADIOPHARMACEUTICAL("Radiofármaco"),
    ADVANCED_THERAPY("Produto de Terapia Avançada"),
    OTHER("Outros");

    companion object {
        /**
         * Resolve o tipo de produto com base na string fornecida no CSV.
         *
         * @param value O texto extraído da coluna do CSV.
         * @return O [AnvisaProductType] correspondente ou [OTHER] caso não seja mapeado.
         */
        fun fromDescription(value: String?): AnvisaProductType {
            if (value.isNullOrBlank()) return OTHER
            return entries.find { it.description.equals(value.trim(), ignoreCase = true) } ?: OTHER
        }
    }
}