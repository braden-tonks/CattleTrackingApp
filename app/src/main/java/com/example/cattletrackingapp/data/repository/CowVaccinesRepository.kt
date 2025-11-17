package com.example.cattletrackingapp.data.repository

import com.example.cattletrackingapp.data.remote.Api.CowVaccinesApi
import com.example.cattletrackingapp.ui.screens.Vaccinations.TargetGroup
import jakarta.inject.Singleton
import javax.inject.Inject

@Singleton
class CowVaccinesRepository @Inject constructor(
    private val api: CowVaccinesApi
) {

    suspend fun getCowVaccineByAnimalId(id: String) = api.getCowVaccineByAnimalId(id)
        /**
         * Builds rows and inserts them. Returns how many rows were written.
         */
        suspend fun insertMany(
            vaccines: List<String>,
            group: TargetGroup,
            animalIds: List<String>,
            dateGiven: String,
            remarks: String?
        ): Int {
            val rows = buildList {
                for (vId in vaccines) {
                    for (aId in animalIds) {
                        val row = when (group) {
                            TargetGroup.Cows -> mapOf(
                                "vaccine_id" to vId,
                                "cow_id" to aId,
                                "date_given" to dateGiven,
                                "remarks" to remarks
                            )
                            TargetGroup.Bulls -> mapOf(
                                "vaccine_id" to vId,
                                "bull_id" to aId,
                                "date_given" to dateGiven,
                                "remarks" to remarks
                            )
                            TargetGroup.Calves -> mapOf(
                                "vaccine_id" to vId,
                                "calf_id" to aId,
                                "date_given" to dateGiven,
                                "remarks" to remarks
                            )
                        }
                        add(row)
                    }
                }
            }
            // one bulk call (or loop calling api.insertCowVaccine(row))
            api.insertMany(rows)
            return rows.size
        }
    }
       // return api.getCowVaccineByAnimalId(Id)
