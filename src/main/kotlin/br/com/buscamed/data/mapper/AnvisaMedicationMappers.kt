package br.com.buscamed.data.mapper

import br.com.buscamed.data.document.AnvisaMedicationDocument
import br.com.buscamed.data.document.AnvisaMedicationLeafletDocument
import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaProductType
import br.com.buscamed.domain.model.anvisa.enumeration.MedicationStripe

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
        hospitalRestriction = this.isHospitalRestriction,
        stripe = this.stripe.name,
        hasLeaflet = this.hasLeaflet,
        hasLeafletPatientResume = this.hasLeafletPatientResume,
        hasLeafletProfessionalResume = this.hasLeafletProfessionalResume
    )
}

fun AnvisaMedicationDocument.toDomain(): AnvisaMedication {
    return AnvisaMedication(
        ggremCode = this.id ?: "",
        registerNumber = this.registerNumber,
        activeIngredients = this.activeIngredients,
        cnpj = this.cnpj,
        laboratory = this.laboratory,
        ean1 = this.ean1,
        ean2 = this.ean2,
        ean3 = this.ean3,
        productName = this.productName,
        presentation = this.presentation,
        therapeuticClass = this.therapeuticClass,
        productType = AnvisaProductType.fromDescription(this.productType),
        isHospitalRestriction = this.hospitalRestriction,
        stripe = MedicationStripe.fromDescription(this.stripe),
        hasLeaflet = this.hasLeaflet,
        hasLeafletPatientResume = this.hasLeafletPatientResume,
        hasLeafletProfessionalResume = this.hasLeafletProfessionalResume
    )
}

fun AnvisaMedicationLeaflet.toDocument(): AnvisaMedicationLeafletDocument {
    return AnvisaMedicationLeafletDocument(
        id = id.name,
        leafletStoragePath = leafletStoragePath,
        leafletResume = leafletResume,
        leafletResumeCreatedAt = leafletResumeCreatedAt,
        leafletResumeUpdatedAt = leafletResumeUpdatedAt
    )
}

fun AnvisaMedicationLeafletDocument.toDomain(): AnvisaMedicationLeaflet {
    return AnvisaMedicationLeaflet(
        id = AnvisaMedicationLeafletIdentifier.valueOf(this.id),
        leafletStoragePath = this.leafletStoragePath,
        leafletResume = this.leafletResume,
        leafletResumeCreatedAt = this.leafletResumeCreatedAt,
        leafletResumeUpdatedAt = this.leafletResumeUpdatedAt
    )
}