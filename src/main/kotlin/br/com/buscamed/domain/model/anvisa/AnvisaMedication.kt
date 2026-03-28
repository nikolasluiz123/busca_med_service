package br.com.buscamed.domain.model.anvisa

import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaProductType
import br.com.buscamed.domain.model.anvisa.enumeration.MedicationStripe

/**
 * Entidade de domínio que representa um medicamento processado a partir dos dados abertos da ANVISA.
 *
 * @property ggremCode Código GGREM único da apresentação do medicamento.
 * @property activeIngredients Lista de princípios ativos que compõem o medicamento.
 * @property cnpj CNPJ do laboratório responsável.
 * @property laboratory Nome do laboratório produtor ou importador.
 * @property ean1 Código de barras principal (GTIN).
 * @property ean2 Código de barras secundário, se existir.
 * @property ean3 Código de barras terciário, se existir.
 * @property productName Nome comercial do produto.
 * @property presentation Descrição da forma farmacêutica, dosagem e quantidade.
 * @property therapeuticClass Classificação anatômica e terapêutica.
 * @property productType Categoria do produto regulatório.
 * @property isHospitalRestriction Indica se a venda é restrita a hospitais.
 * @property stripe Classificação da necessidade de prescrição (tarja).
 */
data class AnvisaMedication(
    val ggremCode: String,
    val activeIngredients: List<String>,
    val cnpj: String,
    val laboratory: String,
    val ean1: String?,
    val ean2: String?,
    val ean3: String?,
    val productName: String,
    val presentation: String,
    val therapeuticClass: String,
    val productType: AnvisaProductType,
    val isHospitalRestriction: Boolean,
    val stripe: MedicationStripe
)