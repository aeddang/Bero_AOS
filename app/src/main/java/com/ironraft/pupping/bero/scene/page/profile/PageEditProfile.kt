package com.ironraft.pupping.bero.scene.page.profile

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.ironraft.pupping.bero.store.provider.model.Gender
import java.time.LocalDate
enum class ProfileEditType {
    Name, Gender, Birth, Introduction, Weight, Height, Immun, Hash, AnimalId, Microchip;
    val description: String
        get() = when (this) {
            Name -> "Edit Name"
            Gender -> "Edit Gender"
            Birth -> "Edit Age"
            Introduction ->  "Edit Introduction"
            Weight -> "Edit Weight"
            Height -> "Edit Height"
            Immun -> "Edit Immunization"
            Hash -> "Edit Tags"
            AnimalId -> "Edit Animal ID"
            Microchip -> "Edit microchip"
        }

    val caption: String?
        get() = when (this) {
            Name -> "Name"
            Weight -> "Weight (kg)"
            Height -> "Height (cm)"
            Birth -> "Select your birthday"
            Immun -> "Select all that applies"
            AnimalId -> "Animal ID"
            Microchip -> "Microchip"
            else -> null
        }


    val placeHolder: String
        get() = when (this) {
            Name -> "ex. Bero"
            AnimalId -> "ex) 123456789012345"
            Microchip -> "ex) 123456789"
            else -> ""
        }
    val limitLine: Int
        get() = when (this) {
            Introduction -> 5
            else -> 1
        }
    val limitLength: Int
        get() = when (this) {
            Introduction -> 100
            Weight, Height -> 10
            AnimalId -> 15
            Microchip -> 9
            else -> 20
        }

    val keyboardOptions: KeyboardOptions
        get() = when (this) {
            Name -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
            Microchip, AnimalId  -> KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            Weight, Height  -> KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
            Introduction -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            )
            else -> KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        }
}
data class ProfileEditData (
    var name:String? = null,
    var gender:Gender? = null,
    var isNeutralized:Boolean? = null,
    var birth:LocalDate? = null,
    var introduction:String? = null,
    var microchip:String? = null,
    var animalId:String? = null,
    var immunStatus:String? = null,
    var hashStatus:String? = null,
    var weight:Double? = null,
    var size:Double? = null
)