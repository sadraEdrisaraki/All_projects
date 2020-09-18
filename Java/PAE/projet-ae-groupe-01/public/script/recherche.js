import { postData, getData, getDataWithParam } from "./ApiUtils.js";
import { show_devis } from "./visualisationDevis.js";
const API_NAME = "/recherche";
const API_NAME_TOKEN = "/utilisateur/verification";
const API_NAME_AMENAGEMENT = "/amenagement"
const API_CLIENT_NAME = "/client";
const API_CLIENT_DEVIS = "/devis";
var typeUtilisateur = "";
var tabTypeAmenagement = new Array();
var tabClient = new Array();
var nbr_liste_choisi = 0;
var selectRecherche = { selectRecherche: "0" };
var mouseX;
var mouseY;
var selectionPrecedente = "0";
$(document).mousemove(function (e) {
	// mouse coordinates
	mouseX = e.pageX;
	mouseY = e.pageY;

});
$(document).ready(function () {

	$("#menu_btn_recherche").click(e => {
		nbr_liste_choisi = 0;
		selectionPrecedente = "0";
		$("#listeRecherche").hide();

		getData(API_NAME_TOKEN, localStorage.getItem("token"), onGetVerification, onErrorVerification);
	});

});


function onGetVerification(response) {
	typeUtilisateur = response.autorisation;
	if (typeUtilisateur == "O" || typeUtilisateur == "P") {
		afficherCadreRecherche();
	};
};

function onErrorVerification(response) {
	console.log("erreur reception verification du type de l'utilisateur ");
};

function afficherCadreRecherche() {
	$('body > :not(#header_accueil_ouvrier)').hide();
	$("#recherche").empty();
	$("#recherche").append(
		' <select class="select_type_amenagement" id="select_type_recherche">'
		+ '<option value="0" style="color: grey;">--sélectionnez un type de recherche --</option>'
		+ '<option value="1">lister devis client</option>'
		+ '<option value="2">lister utilisateurs</option>'
		+ '<option value="3">lister clients</option>'
		+ '</select><br><br>'
		/*div recherche pour devis/realisation*/
		+ '<div id="critere_recherche_devis">'
		+ '<datalist id="liste_client_recherche"></datalist>'
		+ '<span id="span_recherche_client">Nom du Client: </span>'
		+ '<input autocomplete="off" id="input_client_recherche_critere" list="liste_client_recherche"><br><br>'
		+ '<span id="span_recherche_montant">Montant total compris entre: </span>'
		+ '<input type="number" min="0" id="recherche_devis_client_montant1"> et '
		+ '<input type="number" min="0" id="recherche_devis_client_montant2"><br><br>'
		+ '<span id="span_recherche_date"> Date: </span>'
		+ '<input type="date" id="input_date_devis_recherche" min="1899-01-01"><br><br>'
		+ '</div>'
		/*div recherche pour utilisateur */
		+ '<div id="critere_recherche_utilisateurs">'
		+ '<span id="span_recherche_utilisateur_nom">Nom : </span>'
		+ '<input id="input_utilisateur_recherche_nom_utilisateur" ><br><br>'
		+ '<span id="span_recherche_utilisateur_ville">Ville: </span>'
		+ '<input id="input_utilisateur_recherche_ville_utilisateur" ><br><br>'
		+ '</div>'
		/*div recherche pour clients */
		+ '<div id="critere_recherche_clients">'
		+ '<span id="span_recherche_client_nom">Nom : </span>'
		+ '<input id="input_client_recherche_nom_client" ><br><br>'
		+ '<span id="span_recherche_client_code_postal">Code postal : </span>'
		+ '<input id="input_client_recherche_code_postal" ><br><br>'
		+ '<span id="span_recherche_client_ville">Ville : </span>'
		+ '<input id="input_client_recherche_ville_client" ><br><br>'
		+ '</div>'
		/*boutton pour recherche*/
		+ '<button id="btn_rechercher" class="btn_Rectangle btn_form" style="display:none">Rechercher</button>'
	);
	cacherTabRecherche();
	$("#critere_recherche_devis").hide();
	$("#critere_recherche_clients").hide();
	$("#critere_recherche_utilisateurs").hide();
	/*change la variable de selection de recherche a chaque fois qu on click sur le menu
	 * et qu on choisit
	 */
	$("#select_type_recherche").change(function () {

		selectRecherche = { selectRecherche: $(this).children("option:selected").val(), nomUtilisateur: "", villeUtilisateur: "", nomClient: "", villeClient: "", codePostalClient: "", nomClientDevis: "", montantTotal1: "", montantTotal2: "", date: "", listeAmenagement: "" };
		$("#btn_rechercher").show();
		cacherTabRecherche();

		/*CHANGEMENT Recherche ici*/
		/*on met tout les input a ""*/
		SetValueEmpty();

		switch (selectRecherche.selectRecherche) {

			case "1":/*devis*/
				$("#critere_recherche_clients").hide();
				$("#critere_recherche_utilisateurs").hide();
				getData(API_CLIENT_NAME, localStorage.getItem("token"), onGetRechercheClient, onErrorVerification)
				getData(API_NAME_AMENAGEMENT, localStorage.getItem("token"), onGetRechercheAmenagement, onErrorVerification)
				break;
			case "2":/*utilisateurs*/
				$("#critere_recherche_clients").hide();
				$("#critere_recherche_devis").hide();
				$("#critere_recherche_utilisateurs").show();

				break;
			case "3":/*clients*/
				$("#critere_recherche_utilisateurs").hide();
				$("#critere_recherche_devis").hide();
				$("#critere_recherche_clients").show();

				break;
			default:
		}

	});
	console.log("recherche changement" + selectRecherche.selectRecherche);

	$("#liste_recherche").show();
	$("#btn_rechercher").click(e => {
		/*	console.log("select preceden"+selectionPrecedente);
			console.log("select actuel"+JSON.stringify(selectRecherche))*/
		remplissageCritereRecherche();
		if (selectionPrecedente != JSON.stringify(selectRecherche)) {
			console.log("recherche differente");
			selectionPrecedente = JSON.stringify(selectRecherche);//on sauvegarde le type de recherche
			/*faire les modifs dans le select recherche*/

			postData(API_NAME, selectRecherche, localStorage.getItem("token"), onGetRecherche, onErrorRecherche);

		}

	});


};
function cacherTabRecherche() {
	$("#listeRecherche").hide();
	$("#recheche_devis_client_div").hide();
}
function SetValueEmpty() {
	/*set devis/realisation input*/
	setValueCritereRecherche("input_client_recherche_critere");
	setValueCritereRecherche("recherche_devis_client_montant1");
	setValueCritereRecherche("recherche_devis_client_montant2");
	setValueCritereRecherche("input_date_devis_recherche");
	/*set client input*/
	setValueCritereRecherche("input_client_recherche_nom_client");
	setValueCritereRecherche("input_client_recherche_ville_client");
	setValueCritereRecherche("input_client_recherche_code_postal");
	/*set utilisateur input*/
	setValueCritereRecherche("input_utilisateur_recherche_nom_utilisateur");
	setValueCritereRecherche("input_utilisateur_recherche_ville_utilisateur");

};
/*remet a zero toutes les variables stocker dans le json*/

function setValueCritereRecherche(variable) {
	var s = document.getElementById(variable);
	s.value = "";
};

function remplissageCritereRecherche() {

	/*remplissage critère devis realisation*/
	selectRecherche.nomClientDevis = $("#input_client_recherche_critere").val();
	selectRecherche.montantTotal1 = $("#recherche_devis_client_montant1").val();
	selectRecherche.montantTotal2 = $("#recherche_devis_client_montant2").val();
	selectRecherche.date = $("#input_date_devis_recherche").val()
	remplissageTableAmenagement();
	/*remplissage critère client*/
	selectRecherche.nomClient = $("#input_client_recherche_nom_client").val();
	selectRecherche.villeClient = $("#input_client_recherche_ville_client").val();
	selectRecherche.codePostalClient = $("#input_client_recherche_code_postal").val();

	/*remplissage critère utilisateur*/
	selectRecherche.nomUtilisateur = $("#input_utilisateur_recherche_nom_utilisateur").val();
	selectRecherche.villeUtilisateur = $("#input_utilisateur_recherche_ville_utilisateur").val();
	console.log(selectRecherche);
}
function remplissageTableAmenagement() {
	var StringValuetabAmenagement = "";
	for (let index = 0; index < nbr_liste_choisi; index++) {

		let liste_amenagement = document.getElementById('select_type_amenagement_recherche_' + (index + 1));
		if ((liste_amenagement.options[liste_amenagement.selectedIndex].value) != 0) {
			StringValuetabAmenagement += liste_amenagement.options[liste_amenagement.selectedIndex].value + ",";
		}
	};
	StringValuetabAmenagement = StringValuetabAmenagement.substring(0, StringValuetabAmenagement.length - 1);
	selectRecherche.listeAmenagement = StringValuetabAmenagement;
};
/*on récupere la liste des clients */
function onGetRechercheClient(response) {
	/*insertion autocompletitonClient Recherche*/

	let dataliste = document.getElementById("liste_client_recherche");
	for (let i = 0; i < response.length; i++) {
		let option = document.createElement('option');
		option.setAttribute('value', response[i].nom);
		let nomListe = response[i].nom + " " + response[i].prenom + " (" + response[i].email + ")";
		option.innerHTML = nomListe;
		option.id = "client_recherche" + i;
		option.className = "client_auto_completion";
		dataliste.appendChild(option);
		tabClient.push(response[i].email);
	}
	$("#critere_recherche_devis").show();

};

function construireListe() {
	if (nbr_liste_choisi == 0) {
		nbr_liste_choisi++;
		let select = document.createElement('select');
		select.className = "select_type_amenagement";
		let nomId = "select_type_amenagement_recherche_" + nbr_liste_choisi;
		select.id = nomId;
		// Créer l'option par défaut
		let optionDefault = document.createElement('option');
		optionDefault.setAttribute('value', 0);
		optionDefault.innerHTML = "--sélectionnez un type d'aménagement--";
		optionDefault.style.color = "grey";
		optionDefault.opacity = "0.8";
		select.appendChild(optionDefault);

		// Rajoute les types à la liste
		tabTypeAmenagement.forEach(element => {
			let option = document.createElement('option');
			option.setAttribute('value', element.id_amenagement);
			option.innerHTML = element.nom;
			select.appendChild(option);
		});

		/*saut de ligne dans les recherches*/
		let brHtml = document.createElement('BR');
		brHtml.id = "brRecherche";
		insertAfter(document.getElementById("input_date_devis_recherche"), brHtml);
		brHtml = document.createElement('BR');
		brHtml.id = "brRecherche2";
		insertAfter(document.getElementById("brRecherche"), brHtml);

		/*insertion des types aménagements pour les recherches*/
		insertAfter(document.getElementById("brRecherche2"), select);
		select.addEventListener('change', construireListeSuivante);
	}
}

function construireListeSuivante() {

	nbr_liste_choisi++;
	let select_previous_name = "select_type_amenagement_recherche_" + (nbr_liste_choisi - 1);

	let select = document.createElement('select');
	select.className = "select_type_amenagement";
	let nomId = "select_type_amenagement_recherche_" + nbr_liste_choisi;
	select.id = nomId;

	// Créer l'option par défaut
	let optionDefault = document.createElement('option');
	optionDefault.setAttribute('value', 0);
	optionDefault.innerHTML = "--sélectionnez un type d'aménagement--";
	optionDefault.style.color = "grey";
	optionDefault.opacity = "0.8";
	select.appendChild(optionDefault);

	// On retire les éléments déjà séléctionné
	let id_selectionne = document.getElementById(select_previous_name).selectedIndex;
	tabTypeAmenagement.splice(id_selectionne - 1, 1);
	tabTypeAmenagement.forEach(element => {
		let option = document.createElement('option');
		option.setAttribute('value', element.id_amenagement);
		option.innerHTML = element.nom;
		select.appendChild(option);
	});

	insertAfter(document.getElementById(select_previous_name), select)
	//document.getElementById("").insertBefore(select , document.getElementById("btn_rechercher"));
	document.getElementById(select_previous_name).removeEventListener('change', construireListeSuivante);
	select.addEventListener('change', construireListeSuivante);


}

/*TODO AMENAGEMENT RECURSIF*/
function onGetRechercheAmenagement(response) {
	tabTypeAmenagement = response;
	construireListe();
	$("#critere_recherche_devis").append();

};

function onGetRecherche(response) {
	afficherTableauRecherche(response);

};
function onErrorRecherche(response) {
	$("#recheche_devis_client_div").empty();
	$("#recheche_devis_client_div").hide();
	console.log("erreur recherche");
};

function afficherTableauRecherche(response) {
	$("#listeRecherche").empty();
	$("#recheche_devis_client_div").empty();
	$("#recheche_devis_client_div").hide();
	let tableRecherche = response;
	let state = "";


	/*devis*/
	switch (selectRecherche.selectRecherche) {
		case "1":
			for (let i = 0; i < tableRecherche.length; i++) {
				let photoPrefereURL = "<img src=./image/image_default.png class='imageflottanteProfil' alt=...>";
				if (tableRecherche[i].photoPrefere != null) {
					photoPrefereURL = "<img src=" + tableRecherche[i].photoPrefere + " class='imageflottanteProfil' alt=...>";
				}
				let dateTravaux = "non-défini";
				if (tableRecherche[i].dateDebutTravaux != "null") {
					dateTravaux = tableRecherche[i].dateDebutTravaux;
				}
				/*on split les amenagements*/


				switch (tableRecherche[i].etatDesAmenagements) {
					case 'DI':
						state = "Devis introduit";
						break;
					case 'CC':
						state = "Commande confirmée";
						break;
					case 'DDTC':
						state = "Date de début de travaux confirmée";
						break;
					case 'MF':
						state = "Facture de milieu de chantier envoyée";
						break;
					case 'FF':
						state = "Facture de fin de chantier envoyée";
						break;
					case 'DDA':
						state = "Demande d'aménagement annulée"
						break;
					default:
						state = "Visible";
				};

				/*on crée les aménagements*/

				let tabAmenagement = (tableRecherche[i].typeAmenagement).split(",");
				let amenagement = "<p>type(s) d'aménagement(s):"
				for (let j = 0; j < tabAmenagement.length; j++) {
					amenagement += "<span  class=divTypeAmenagement>" + tabAmenagement[j] + "</span>"
				};
				amenagement += "</p>";

				$('#recheche_devis_client_div').append(

					"<div id=" + "devis_client_" + tableRecherche[i].idDevis + " class=divRecherche>"
					+ photoPrefereURL
					+ amenagement
					+ "<p>prénom du client:" + tableRecherche[i].prenom + "</p>"
					+ "<p>montant:" + tableRecherche[i].montantTotal + "€" + "</p>"
					+ "<p>date:" + tableRecherche[i].dateDevis + "</p>"
					+ "<p>durée des travaux:" + tableRecherche[i].dureeTravaux + "jours" + "</p>"
					+ "<p>photo(s) avant aménagement(s):" + tableRecherche[i].nombrePhotosAvant + "</p>"
					+ "<p>photo(s) après aménagement(s):" + tableRecherche[i].nombrePhotosApres + "</p>"
					+ "<p>date de début de travaux:" + dateTravaux + "</p>"
					+ "<p>état des travaux: <span  class=divTypeAmenagement+>" + state + "</span></p>"
					+ "</div>"
				);
				/*deplacer la position de la bule d info*/
				$("#devis_client_" + tableRecherche[i].idDevis).mouseover(function () {

					$('#tooltip span').html("cliquez pour avoir les détails du devis");
					$(this).css({ 'top': mouseY - 75, 'left': mouseX - 100 });
					$('#tooltip').stop(false, true).fadeIn(1);

					$(this).mousemove(function () {
						$('#tooltip').css({ 'top': mouseY - 75, 'left': mouseX - 100 });
					});
				}).mouseout(function () {
					// hide tooltip
					$('#tooltip').stop(false, true).fadeOut('slow');
				});

				/* affichage du devis*/

				$("#devis_client_" + tableRecherche[i].idDevis).click(function () {
					let id = $(this).attr("id");
					id = id.split("_")[2];
					console.log("Load visualisation devis" + id);
					show_devis(tableRecherche[i].idDevis);
				})
			}


			$("#recheche_devis_client_div").show();
			break;

		case "2":
			$("#listeRecherche").append(
				'<thead class="enTeteTableau">'
				+ '<tr>'
				+ '<th>Nom</th>'
				+ '<th>Prenom</th>'
				+ '<th>Ville</th>'
				+ '<th>e-mail</th>'
				+ '<th>Pseudo</th>'
				+ '<th>Type de compte</th>'
				+ "<th>Date d'inscription</th>"
				+ '</tr>'
				+ '</thead>'
				+ '<tbody id="tbody_listeRecherche">'
				+ '</tbody>'
			);
			for (let i = 0; i < tableRecherche.length; i++) {
				switch (tableRecherche[i].type) {
					case 'C':
						state = "Client";
						break;
					case 'O':
						state = "Ouvrier";
						break;
					case 'P':
						state = "Patron";
						break;
					default:
						state = "Utilisateur";
				}
				$("#tbody_listeRecherche").append(
					"<tr>"
					+ '<td>' + tableRecherche[i].nom + '</td>'
					+ "<td>" + tableRecherche[i].prenom + "</td>"
					+ "<td>" + tableRecherche[i].ville + "</td>"
					+ "<td>" + tableRecherche[i].email + "</td>"
					+ "<td>" + tableRecherche[i].pseudo + "</td>"
					+ "<td>" + state + "</td>"
					+ "<td>" + tableRecherche[i].dateInscription + "</td>"
					+ "</tr>"
				);
			}
			$("#listeRecherche").show();
			break;
		case "3":
			$("#listeRecherche").append(
				'<thead class="enTeteTableau">'
				+ '<tr>'
				+ '<th>Nom</th>'
				+ '<th>Prenom</th>'
				+ '<th>Ville</th>'
				+ '<th>Code Postal</th>'
				+ '<th>Rue</th>'
				+ '<th>Numero</th>'
				+ '<th>N° Boite</th>'
				+ '<th>Telephone</th>'
				+ '<th>E-mail</th>'
				+ '</tr>'
				+ '</thead>'
				+ '<tbody id="tbody_listeRecherche">'
				+ '</tbody>'
			);
			let boite;

			for (let i = 0; i < tableRecherche.length; i++) {
				if (tableRecherche[i].boite == null) {
					boite = "non définie";
				}
				else {
					boite = tableRecherche[i];
				}
				$("#tbody_listeRecherche").append(
					"<tr" + " id=" + tableRecherche[i].idClient + ">"
					+ '<td>' + tableRecherche[i].nom + '</td>'
					+ "<td>" + tableRecherche[i].prenom + "</td>"
					+ "<td>" + tableRecherche[i].ville + "</td>"
					+ "<td>" + tableRecherche[i].codePostal + "</td>"
					+ "<td>" + tableRecherche[i].rue + "</td>"
					+ "<td>" + tableRecherche[i].numero + "</td>"
					+ "<td>" + boite + "</td>"
					+ "<td>" + tableRecherche[i].telephone + "</td>"
					+ "<td>" + tableRecherche[i].email + "</td>"
					+ "</tr>"
				);
				$("#" + tableRecherche[i].idClient).click(function () {
					getDataWithParam(API_CLIENT_DEVIS, tableRecherche[i].idClient, localStorage.getItem("token"), onGetRechercheDansListeClient, onErrorRecherche);
				});
			}

		

			$("#listeRecherche").show();
			break;
		default:
	}


};
function onGetRechercheDansListeClient(response) {
	$("#listeRecherche").empty();
	$("#recheche_devis_client_div").empty();
	$("#recheche_devis_client_div").hide();

	let tableRecherche = response;
	let state = "";

	for (let i = 0; i < tableRecherche.length; i++) {
		let photoPrefereURL = "<img src=./image/image_default.png class='imageflottanteProfil' alt=...>";
		if (tableRecherche[i].photoPrefere != null) {
			photoPrefereURL = "<img src=" + tableRecherche[i].photoPrefere + " class='imageflottanteProfil' alt=...>";
		}
		let dateTravaux = "non-défini";
		if (tableRecherche[i].dateDebutTravaux != "null") {
			dateTravaux = tableRecherche[i].dateDebutTravaux;
		}
		/*on split les amenagements*/


		switch (tableRecherche[i].etatDesAmenagements) {
			case 'DI':
				state = "Devis introduit";
				break;
			case 'CC':
				state = "Commande confirmée";
				break;
			case 'DDTC':
				state = "Date de début de travaux confirmé";
				break;
			case 'MF':
				state = "Facture de milieu de chantier envoyé";
				break;
			case 'FF':
				state = "Facture de fin de chantier envoyé";
				break;
			case 'DDA':
				state = "Demande d'aménagement annulé"
				break;
			default:
				state = "Visible";
		};

		/*on crée les aménagements*/

		let tabAmenagement = (tableRecherche[i].typeAmenagement).split(",");
		let amenagement = "<p>type(s) d'aménagement(s):"
		for (let j = 0; j < tabAmenagement.length; j++) {
			amenagement += "<span  class=divTypeAmenagement>" + tabAmenagement[j] + "</span>"
		};
		amenagement += "</p>";

		$('#recheche_devis_client_div').append(

			"<div id=" + "devis_client_" + tableRecherche[i].idDevis + " class=divRecherche>"
			+ photoPrefereURL
			+ amenagement
			+ "<p>montant:" + tableRecherche[i].montantTotal + "€" + "</p>"
			+ "<p>date:" + tableRecherche[i].dateDevis + "</p>"
			+ "<p>durée des travaux:" + tableRecherche[i].dureeTravaux + "jours" + "</p>"
			+ "<p>photo(s) avant aménagement(s):" + tableRecherche[i].nombrePhotosAvant + "</p>"
			+ "<p>photo(s) après aménagement(s):" + tableRecherche[i].nombrePhotosApres + "</p>"
			+ "<p>date de début de travaux:" + dateTravaux + "</p>"
			+ "<p>prénom du client:" + tableRecherche[i].prenom + "</p>"
			+ "<p>état des travaux: <span  class=divTypeAmenagement+>" + state + "</span></p>"
			+ "</div>"
		);
		/*deplacer la position de la bule d info*/
		$("#devis_client_" + tableRecherche[i].idDevis).mouseover(function () {

			$('#tooltip span').html("cliquez pour avoir les détails du devis");
			$(this).css({ 'top': mouseY - 75, 'left': mouseX - 100 });
			$('#tooltip').stop(false, true).fadeIn(1);

			$(this).mousemove(function () {
				$('#tooltip').css({ 'top': mouseY - 75, 'left': mouseX - 100 });
			});
		}).mouseout(function () {
			// hide tooltip
			$('#tooltip').stop(false, true).fadeOut('slow');
		});

		/* affichage du devis*/

		$("#devis_client_" + tableRecherche[i].idDevis).click(function () {
			let id = $(this).attr("id");
			id = id.split("_")[2];
			console.log("Load visualisation devis" + id);
			show_devis(tableRecherche[i].idDevis);
		})
	}
	$("#recheche_devis_client_div").show();
	
};

function insertAfter(referenceNode, newNode) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
};
