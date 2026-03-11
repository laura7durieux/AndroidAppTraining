package com.example.immofollow.data.defaults

import com.example.immofollow.data.model.ScoreBand
import com.example.immofollow.data.model.SpecDefinition
import com.example.immofollow.data.model.SpecInputType

object DefaultSpecs {

    fun all(): List<SpecDefinition> = listOf(
        numberSpec(
            id = "prix",
            name = "Prix",
            category = "Financière",
            flexibility = "F1",
            weight = 5,
            bands = listOf(
                ScoreBand("150-200k", 3, 150000.0, 200000.0),
                ScoreBand("200-250k", 2, 200000.0, 250000.0),
                ScoreBand("250-300k", 1, 250000.0, 300000.0),
                ScoreBand(">300k", 0, 300000.0, null)
            )
        ),
        numberSpec(
            id = "temps_strasbourg",
            name = "Temps Strasbourg",
            category = "Financière",
            flexibility = "F0",
            weight = 5,
            bands = listOf(
                ScoreBand("15-25 min", 3, 15.0, 25.0),
                ScoreBand("25-35 min", 2, 25.0, 35.0),
                ScoreBand("35-45 min", 1, 35.0, 45.0),
                ScoreBand(">45 min", 0, 45.0, null)
            )
        ),
        numberSpec(
            id = "temps_bouxwiller",
            name = "Temps Bouxwiller",
            category = "Financière",
            flexibility = "F1",
            weight = 4,
            bands = listOf(
                ScoreBand("15-25 min", 3, 15.0, 25.0),
                ScoreBand("25-35 min", 2, 25.0, 35.0),
                ScoreBand("35-45 min", 1, 35.0, 45.0),
                ScoreBand(">45 min", 0, 45.0, null)
            )
        ),
        numberSpec(
            id = "temps_merzwiller",
            name = "Temps Merzwiller",
            category = "Financière",
            flexibility = "F2",
            weight = 2,
            bands = listOf(
                ScoreBand("15-25 min", 3, 15.0, 25.0),
                ScoreBand("25-35 min", 2, 25.0, 35.0),
                ScoreBand("35-45 min", 1, 35.0, 45.0),
                ScoreBand(">45 min", 0, 45.0, null)
            )
        ),
        choiceSpec(
            id = "isolation",
            name = "Isolation",
            category = "Financière",
            flexibility = "F1",
            weight = 4,
            options = listOf("A", "B", "C", "D", "E", "F", "G"),
            bands = listOf(
                ScoreBand("A-B", 3, acceptedTexts = listOf("A", "B")),
                ScoreBand("C", 2, acceptedTexts = listOf("C")),
                ScoreBand("D", 1, acceptedTexts = listOf("D")),
                ScoreBand("E-F", 0, acceptedTexts = listOf("E", "F")),
                ScoreBand("G", -1, acceptedTexts = listOf("G"))
            )
        ),
        choiceSpec(
            id = "chauffage",
            name = "Chauffage",
            category = "Financière",
            flexibility = "F1",
            weight = 3,
            options = listOf(
                "Pompe à chaleur",
                "Poêle à pellets",
                "Electricité (hors convecteur)",
                "Fuel / convecteur",
                "Gaz"
            ),
            bands = listOf(
                ScoreBand("Pompe à chaleur", 3, acceptedTexts = listOf("Pompe à chaleur")),
                ScoreBand("Poêle à pellets", 2, acceptedTexts = listOf("Poêle à pellets")),
                ScoreBand("Electricité (hors convecteur)", 1, acceptedTexts = listOf("Electricité (hors convecteur)")),
                ScoreBand("Fuel / convecteur", 0, acceptedTexts = listOf("Fuel / convecteur")),
                ScoreBand("Gaz", -1, acceptedTexts = listOf("Gaz"))
            )
        ),
        numberSpec(
            id = "surface_habitable",
            name = "Surface habitable",
            category = "Superficie",
            flexibility = "F0",
            weight = 5,
            bands = listOf(
                ScoreBand("200-225 m²", 3, 200.0, 225.0),
                ScoreBand("175-200 m²", 2, 175.0, 200.0),
                ScoreBand("150-175 m²", 1, 150.0, 175.0),
                ScoreBand("<150 m²", 0, 100.0, 149.99),
                ScoreBand("<100 m²", -1, null, 99.99)
            )
        ),
        booleanSpec(
            id = "surface_renovable",
            name = "Surface renovable",
            category = "Superficie",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        numberSpec(
            id = "surface_exterieur_entretien",
            name = "Surface exterrieur à entretenir",
            category = "Superficie",
            flexibility = "F0",
            weight = 3,
            bands = listOf(
                ScoreBand("7-8 ares", 3, 7.0, 8.0),
                ScoreBand("5-7 ares", 2, 5.0, 7.0),
                ScoreBand("4-5 ares", 1, 4.0, 5.0),
                ScoreBand("<4 ares", 0, 0.0, 3.99)
            )
        ),
        booleanSpec(
            id = "surface_exterieur_plus",
            name = "Surface exterieur en plus (cours)",
            category = "Superficie",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "dependance_atelier",
            name = "Dépence - atelier",
            category = "Superficie",
            flexibility = "F2",
            weight = 2,
            trueScore = 1,
            falseScore = 0
        ),
        numberSpec(
            id = "distance_voisins",
            name = "Distance aux voisins",
            category = "Superficie",
            flexibility = "F1",
            weight = 4,
            bands = listOf(
                ScoreBand("Pas de voisin / très éloigné", 3, 25.0, null),
                ScoreBand("15 m", 2, 15.0, 24.99),
                ScoreBand("10 m", 1, 10.0, 14.99),
                ScoreBand("<10 m", 0, 5.0, 9.99),
                ScoreBand("<5 m", -1, 0.0, 4.99)
            )
        ),
        numberSpec(
            id = "nb_chambre",
            name = "Nb Chambre",
            category = "Chambre",
            flexibility = "F1",
            weight = 5,
            bands = listOf(
                ScoreBand("6", 3, 6.0, null),
                ScoreBand("5", 2, 5.0, 5.99),
                ScoreBand("4", 1, 4.0, 4.99),
                ScoreBand("3", 0, 3.0, 3.99),
                ScoreBand("<3", -1, null, 2.99)
            )
        ),
        numberSpec(
            id = "taille_chambres",
            name = "Tailles chambres",
            category = "Chambre",
            flexibility = "F2",
            weight = 3,
            bands = listOf(
                ScoreBand("25-30 m²", 3, 25.0, 30.0),
                ScoreBand("20-25 m²", 2, 20.0, 25.0),
                ScoreBand("15-20 m²", 1, 15.0, 20.0),
                ScoreBand("12-15 m²", 0, 12.0, 15.0),
                ScoreBand("<12 m²", -1, null, 11.99)
            )
        ),
        numberSpec(
            id = "salon_taille",
            name = "Salon taille",
            category = "Salon",
            flexibility = "F1",
            weight = 3,
            bands = listOf(
                ScoreBand(">55 m²", 3, 55.0, null),
                ScoreBand("45-55 m²", 2, 45.0, 55.0),
                ScoreBand("40-45 m²", 1, 40.0, 45.0),
                ScoreBand("30-40 m²", 0, 30.0, 40.0),
                ScoreBand("<30 m²", -1, null, 29.99)
            )
        ),
        booleanSpec(
            id = "cheminee_salon",
            name = "Cheminée salon",
            category = "Salon",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "hauteur_plafond_salon",
            name = "Hauteur plafond salon",
            category = "Salon",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        choiceSpec(
            id = "sol_salon",
            name = "Sol",
            category = "Salon",
            flexibility = "F2",
            weight = 3,
            options = listOf("Parquet bois", "Carrelage + tapis", "Autre"),
            bands = listOf(
                ScoreBand("Parquet bois", 1, acceptedTexts = listOf("Parquet bois")),
                ScoreBand("Carrelage + tapis", 0, acceptedTexts = listOf("Carrelage + tapis")),
                ScoreBand("Autre", 0, acceptedTexts = listOf("Autre"))
            )
        ),
        choiceSpec(
            id = "praticite_cuisine",
            name = "Praticité",
            category = "Cuisine",
            flexibility = "F1",
            weight = 4,
            options = listOf("Parfaite", "Très bien", "Bien", "Correcte", "Incorrecte"),
            bands = listOf(
                ScoreBand("Parfaite", 3, acceptedTexts = listOf("Parfaite")),
                ScoreBand("Très bien", 2, acceptedTexts = listOf("Très bien")),
                ScoreBand("Bien", 1, acceptedTexts = listOf("Bien")),
                ScoreBand("Correcte", 0, acceptedTexts = listOf("Correcte")),
                ScoreBand("Incorrecte", -1, acceptedTexts = listOf("Incorrecte"))
            )
        ),
        booleanSpec(
            id = "ilot_central",
            name = "Ilot central",
            category = "Cuisine",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "lave_vaisselle",
            name = "Lave-vaisselle",
            category = "Cuisine",
            flexibility = "F0",
            weight = 4,
            trueScore = 1,
            falseScore = -1
        ),
        booleanSpec(
            id = "amenagement_meuble",
            name = "Amménagement (meuble integrée)",
            category = "Cuisine",
            flexibility = "F0",
            weight = 5,
            trueScore = 1,
            falseScore = -1
        ),
        booleanSpec(
            id = "place_machine_cafe",
            name = "Place pour la machine à café - auto",
            category = "Cuisine",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        choiceSpec(
            id = "evier",
            name = "Evier",
            category = "Cuisine",
            flexibility = "F2",
            weight = 1,
            options = listOf("2 bacs + douchette", "2 bacs", "Autre"),
            bands = listOf(
                ScoreBand("2 bacs + douchette", 2, acceptedTexts = listOf("2 bacs + douchette")),
                ScoreBand("2 bacs", 1, acceptedTexts = listOf("2 bacs")),
                ScoreBand("Autre", 0, acceptedTexts = listOf("Autre"))
            )
        ),
        choiceSpec(
            id = "plaque_cuisson",
            name = "Plaque de cuisson",
            category = "Cuisine",
            flexibility = "F2",
            weight = 2,
            options = listOf("Induction + grande taille", "Induction", "Autre"),
            bands = listOf(
                ScoreBand("Induction + grande taille", 2, acceptedTexts = listOf("Induction + grande taille")),
                ScoreBand("Induction", 1, acceptedTexts = listOf("Induction")),
                ScoreBand("Autre", 0, acceptedTexts = listOf("Autre"))
            )
        ),
        choiceSpec(
            id = "praticite_sdb",
            name = "Praticité",
            category = "Salle de bain",
            flexibility = "F0",
            weight = 4,
            options = listOf("Parfaite", "Très bien", "Bien", "Correcte", "Incorrecte"),
            bands = listOf(
                ScoreBand("Parfaite", 3, acceptedTexts = listOf("Parfaite")),
                ScoreBand("Très bien", 2, acceptedTexts = listOf("Très bien")),
                ScoreBand("Bien", 1, acceptedTexts = listOf("Bien")),
                ScoreBand("Correcte", 0, acceptedTexts = listOf("Correcte")),
                ScoreBand("Incorrecte", -1, acceptedTexts = listOf("Incorrecte"))
            )
        ),
        choiceSpec(
            id = "config_sdb",
            name = "Configuration",
            category = "Salle de bain",
            flexibility = "F1",
            weight = 3,
            options = listOf(
                "Salle douche - bain séparé",
                "Douche et bain",
                "Bain",
                "Juste douche"
            ),
            bands = listOf(
                ScoreBand("Salle douche - bain séparé", 3, acceptedTexts = listOf("Salle douche - bain séparé")),
                ScoreBand("Douche et bain", 2, acceptedTexts = listOf("Douche et bain")),
                ScoreBand("Bain", 0, acceptedTexts = listOf("Bain")),
                ScoreBand("Juste douche", -1, acceptedTexts = listOf("Juste douche"))
            )
        ),
        choiceSpec(
            id = "config_toilette",
            name = "Configuration",
            category = "Toilette",
            flexibility = "F0",
            weight = 4,
            options = listOf(
                "2 séparées de la salle de bain",
                "2 toilettes",
                "1 toilette séparée de la salle de bain",
                "Toilette dans la salle de bain"
            ),
            bands = listOf(
                ScoreBand("2 séparées de la salle de bain", 2, acceptedTexts = listOf("2 séparées de la salle de bain")),
                ScoreBand("2 toilettes", 1, acceptedTexts = listOf("2 toilettes")),
                ScoreBand("1 toilette séparée de la salle de bain", 0, acceptedTexts = listOf("1 toilette séparée de la salle de bain")),
                ScoreBand("Toilette dans la salle de bain", -1, acceptedTexts = listOf("Toilette dans la salle de bain"))
            )
        ),
        choiceSpec(
            id = "machine_laver",
            name = "machine à laver",
            category = "Laverie",
            flexibility = "F0",
            weight = 3,
            options = listOf("Buanderie", "Salle de bain", "Ailleurs"),
            bands = listOf(
                ScoreBand("Buanderie", 1, acceptedTexts = listOf("Buanderie")),
                ScoreBand("Salle de bain", 0, acceptedTexts = listOf("Salle de bain")),
                ScoreBand("Ailleurs", -1, acceptedTexts = listOf("Ailleurs"))
            )
        ),
        booleanSpec(
            id = "petit_atelier",
            name = "Petit atelier",
            category = "Bureau",
            flexibility = "F0",
            weight = 2,
            trueScore = 1,
            falseScore = -1
        ),
        choiceSpec(
            id = "bureau_jb",
            name = "Bureau JB",
            category = "Bureau",
            flexibility = "F0",
            weight = 2,
            options = listOf("Pièce séparée", "Pièce unique", "Absence"),
            bands = listOf(
                ScoreBand("Pièce séparée", 2, acceptedTexts = listOf("Pièce séparée")),
                ScoreBand("Pièce unique", 1, acceptedTexts = listOf("Pièce unique")),
                ScoreBand("Absence", -1, acceptedTexts = listOf("Absence"))
            )
        ),
        choiceSpec(
            id = "bureau_laura",
            name = "Bureau Laura",
            category = "Bureau",
            flexibility = "F0",
            weight = 2,
            options = listOf("Pièce séparée", "Pièce unique", "Absence"),
            bands = listOf(
                ScoreBand("Pièce séparée", 2, acceptedTexts = listOf("Pièce séparée")),
                ScoreBand("Pièce unique", 1, acceptedTexts = listOf("Pièce unique")),
                ScoreBand("Absence", -1, acceptedTexts = listOf("Absence"))
            )
        ),
        choiceSpec(
            id = "atelier_gros_oeuvre",
            name = "Atelier gros oeuvre",
            category = "Atelier",
            flexibility = "F1",
            weight = 4,
            options = listOf("Dépendance", "Pièce propre", "Absence"),
            bands = listOf(
                ScoreBand("Dépendance", 2, acceptedTexts = listOf("Dépendance")),
                ScoreBand("Pièce propre", 1, acceptedTexts = listOf("Pièce propre")),
                ScoreBand("Absence", -1, acceptedTexts = listOf("Absence"))
            )
        ),
        choiceSpec(
            id = "place_voiture",
            name = "Place voiture",
            category = "Garage",
            flexibility = "F1",
            weight = 5,
            options = listOf(
                "Garage 2 voitures",
                "Garage 1 voiture + parking privé",
                "Abris voiture privé",
                "Absence de parking privé"
            ),
            bands = listOf(
                ScoreBand("Garage 2 voitures", 3, acceptedTexts = listOf("Garage 2 voitures")),
                ScoreBand("Garage 1 voiture + parking privé", 2, acceptedTexts = listOf("Garage 1 voiture + parking privé")),
                ScoreBand("Abris voiture privé", 1, acceptedTexts = listOf("Abris voiture privé")),
                ScoreBand("Absence de parking privé", -1, acceptedTexts = listOf("Absence de parking privé"))
            )
        ),
        booleanSpec(
            id = "piscine",
            name = "Piscine",
            category = "Divers",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "potager",
            name = "Potager",
            category = "Divers",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "terrasse",
            name = "Terrasse",
            category = "Divers",
            flexibility = "F2",
            weight = 1,
            trueScore = 1,
            falseScore = 0
        ),
        booleanSpec(
            id = "coup_coeur",
            name = "Coup de coeur",
            category = "Divers",
            flexibility = "F2",
            weight = 5,
            trueScore = 3,
            falseScore = 0,
            visitOnly = true
        )
    )

    private fun numberSpec(
        id: String,
        name: String,
        category: String,
        flexibility: String,
        weight: Int,
        bands: List<ScoreBand>,
        visitOnly: Boolean = false
    ) = SpecDefinition(
        id = id,
        defaultName = name,
        category = category,
        flexibility = flexibility,
        inputType = SpecInputType.NUMBER,
        weight = weight,
        bands = bands,
        visitOnly = visitOnly
    )

    private fun choiceSpec(
        id: String,
        name: String,
        category: String,
        flexibility: String,
        weight: Int,
        options: List<String>,
        bands: List<ScoreBand>,
        visitOnly: Boolean = false
    ) = SpecDefinition(
        id = id,
        defaultName = name,
        category = category,
        flexibility = flexibility,
        inputType = SpecInputType.CHOICE,
        weight = weight,
        bands = bands,
        options = options,
        visitOnly = visitOnly
    )

    private fun booleanSpec(
        id: String,
        name: String,
        category: String,
        flexibility: String,
        weight: Int,
        trueScore: Int,
        falseScore: Int,
        visitOnly: Boolean = false
    ) = SpecDefinition(
        id = id,
        defaultName = name,
        category = category,
        flexibility = flexibility,
        inputType = SpecInputType.BOOLEAN,
        weight = weight,
        bands = listOf(
            ScoreBand("Oui", trueScore, booleanValue = true),
            ScoreBand("Non", falseScore, booleanValue = false)
        ),
        visitOnly = visitOnly
    )
}