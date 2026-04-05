package br.com.buscamed.data.mapper

import br.com.buscamed.data.document.AnvisaMedicationDocument
import br.com.buscamed.domain.model.anvisa.AnvisaMedication

fun AnvisaMedication.toDocument(): AnvisaMedicationDocument {
    return AnvisaMedicationDocument(
        id = this.ggremCode,
        registerNumber = registerNumber,
        activeIngredients = this.activeIngredients,
        cnpj = this.cnpj,
        laboratory = this.laboratory,
        ean1 = this.ean1,
        ean2 = this.ean2,
        ean3 = this.ean3,
        productName = this.productName,
        presentation = this.presentation,
        therapeuticClass = this.therapeuticClass,
        productType = this.productType.name,
        isHospitalRestriction = this.isHospitalRestriction,
        stripe = this.stripe.name,
    )
}