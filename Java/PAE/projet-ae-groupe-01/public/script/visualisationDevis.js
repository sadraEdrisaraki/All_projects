import {getData,postData,putData,getDataWithParam} from "./ApiUtils.js";
const API_NAME_TOKEN= "/utilisateur/verification";
const API_NAME_VISUALISATION="/devis/visualisation";
const API_NAME_CLIENT="/client";
const API_NAME_DATE_DEVIS="/devis/setdate";
const API_IMAGE = "/image";
const API_AMENAGEMENT = "/amenagement";
const API_IMAGE_DEVIS = "/image/devis";
const API_NAME_DEVIS = "/devis"
var id_devis;
var devis;
var client;
var id_client;
var all_type_amenagement;
var nbre_preview = 0;
var date_debut_travaux_correct = false;

$(document).ready(function(){
	var reader = new FileReader(); //Reader pour transformer l'image

  	reader.onloadend = function() {
		nbre_preview += 1;
    	add_image_preview(reader.result,nbre_preview);
  	}

	$( window ).resize(function() {
		align_photo_pref();
	});

	$("#ajout_image_apres_amenagement").change(function(){
		console.log("OnChange");
		var file = $("#ajout_image_apres_amenagement").get()[0].files[0];
		console.log(file);

		reader.readAsDataURL(file);
	})

	$("#info_travaux > #confirmer_commande").click(function(){
		if(date_debut_travaux_correct){
			set_devis_etat("CC");
		}
	})

	$("#info_travaux > #confirmer_changement").click(function(){
		envoye_photo();
	})


	$("#date_travaux_input").change(function(){
		let date = $("#date_travaux_input").val();
		let data = { id_devis : id_devis, date_devis: date }
		console.log("Data" + data);
		postData(API_NAME_DATE_DEVIS , data , localStorage.getItem("token"), onDateConfirme , onErrorDateSet);
	})

	$("#info_travaux > #confirmer_date_debut_travaux").click(function(){
		if(date_debut_travaux_correct){
			set_devis_etat("DDTC");
		}
	})

	$("#info_travaux > #annuler_commande").click(function(){
		set_devis_etat("DDA");
	})

	$("#info_travaux > #confirmer_passer_etape_suivante").click(function(){
		switch (devis.etat_des_amenagements) {
			case 'DDTC':
				if(devis.duree_travaux >= 15 && $("#facture_milieu_envoye").prop("checked") ){
					set_devis_etat("MF");
				}
				else if ($("#facture_fin_envoye").prop("checked")) {
					set_devis_etat("FF");
				}
				break;
			case 'MF':
				if ($("#facture_fin_envoye").prop("checked")) {
					set_devis_etat("FF");
				}
				break;

			case 'FF':
			if ($("#facture_fin_paye").prop("checked")) {
				set_devis_etat("RV");
				envoye_photo();
			}
				break;
		}
	})
})

function show_devis(id_devis_to_show){
	id_devis = id_devis_to_show;
	console.log(id_devis_to_show)
	$("#liste_recherche").hide();
	getData(API_NAME_TOKEN,localStorage.getItem("token"),onGetVerification,onErrorVerification);
	$("#visualisation_devis").show();
};

function show_devis_client(id_devis_to_show){
	id_devis = id_devis_to_show;
	$("#liste_recherche").hide();
	getData(API_NAME_TOKEN,localStorage.getItem("token"),onGetVerificationClient,onErrorVerification);
	getDataWithParam(API_IMAGE_DEVIS,id_devis_to_show,localStorage.getItem("token"),onGetPhotosDevis,onErrorPhotosDevis);
	$("#visualisation_devis").show();
};



// ---------------- onGet ------------------------ //


function onGetVerification(response){
	if(response.autorisation=="O"){
		//on verifie que la personne connecte est bien un ouvrier
		getDataWithParam(API_NAME_VISUALISATION,id_devis,localStorage.getItem("token"),onGetVisualisation,onErrorVisualisation);
	};

};

function onGetVerificationClient(response){
	if(response.autorisation=="C"){
		//on verifie que la personne connecte est bien un ouvrier
		getDataWithParam(API_NAME_VISUALISATION,id_devis,localStorage.getItem("token"),onGetVisualisationClient,onErrorVisualisation);
	};

};

function onGetVisualisation(response){
	devis = response;
	getData(API_AMENAGEMENT,localStorage.getItem("token"),onGetTypeAmenagement,onErrorAmenagement);
	getDataWithParam(API_IMAGE_DEVIS,id_devis,localStorage.getItem("token"),onGetPhotosDevis,onErrorPhotosDevis);
	load_devis_info(response);
	load_client_info(response);
	load_travaux_info(response);
}

function onGetVisualisationClient(response){
	devis = response;
	$("#info_travaux").hide();
	$("#devis_client").hide();
	getData(API_AMENAGEMENT,localStorage.getItem("token"),onGetTypeAmenagement,onErrorAmenagement);
	getDataWithParam(API_IMAGE_DEVIS,id_devis,localStorage.getItem("token"),onGetPhotosDevis,onErrorPhotosDevis);
	$("#info_devis > #etat_travaux_client").text("Etat des aménagements : " + etat_to_string(devis.etat_des_amenagements));
	load_devis_info(response);
	load_client_info(response);
}
function onGetPhotosDevis(response){
	$("#photos_devis > #photo_apres_amenagement").html("");
	$("#photos_devis > #photo_avant_amenagement").html("");
	let date_devis = Date.parse(devis.date_devis);
	$.each(response,function(){
		let date = Date.parse(this.date_photo)
		if(date > date_devis){
			if(this.id_photo == devis.photo_preferee){
				$("#photos_devis > #photo_apres_amenagement").append("<img id=\"photo_preferee\" class=\"images_preview\" src=\""+ this.photo +"\" >");
				$("#photos_devis > #photo_apres_amenagement").append("<img id=\"icone_photo_pref\" class=\"images_preview\" src=\"image/coeur.png\" >");
			}
			else{
				$("#photos_devis > #photo_apres_amenagement").append("<img class=\"images_preview\" src=\""+ this.photo +"\" >");
			}
		}
		else{
			if(this.id_photo == devis.photo_preferee){
				$("#photos_devis > #photo_avant_amenagement").append("<img id=\"photo_preferee\" class=\"images_preview\" src=\""+ this.photo +"\" >");
				$("#photos_devis > #photo_apres_amenagement").append("<img id=\"icone_photo_pref\" class=\"images_preview\" src=\"image/coeur.png\" >");
			}
			else{
				$("#photos_devis > #photo_avant_amenagement").append("<img class=\"images_preview\" src=\""+ this.photo +"\" >");
			}
		}
	});
	//$("#visualisation_devis").show();
	align_photo_pref();
}


function onGetTypeAmenagement(response){
	let type_devis = devis.type_amenagement.split(', ');
	all_type_amenagement = new Array();

	$.each(response,function(){
		if(type_devis.includes(this.nom)){
			console.log("Ajout type"  + this);
			all_type_amenagement.push(this);
		}
	})
	console.log(all_type_amenagement);
}

function onImageAdded(response){
	console.log("Image ajoutée");
	show_devis(id_devis);
}

function onEtatSet(response){
	console.log("Etat modifié");
	show_devis(id_devis);
}

function onDateConfirme(){
	console.log("Date modifiée avec succès");
	date_debut_travaux_correct = true;
	$("#error_date_debut_travaux").hide();
}

function onDateSet(response){
	console.log("Date mise a jour");
	$("#error_date_debut_travaux").hide();
	set_devis_etat("CC");
}

//--------- onError -------------//

function onErrorPhotosDevis(response){
	console.log("Erreur photos devis");
	console.log(response);
}

function onErrorVisualisation(response){
	console.log("Erreur visualisation devis");
	console.log(response);
}

function onErrorEtatSet(response){
	console.log("Etat non modifié");
}

function onErrorDateSet(response){
	console.log("Erreur lors de la mise a jour de la date");
	date_debut_travaux_correct = false;
	$("#error_date_debut_travaux").show();
}

function onErrorVerification(response){
	console.log("erreur reception verification du type de l'utilisateur ");
};

function onErrorListeDevis(response){
    console.log("error affichage liste des devis pour un client ");
};

function onErrorAmenagement(response){
	console.log(response);
	console.log("Erreur lors de la récupération des amenagements");
}

function onImageNotAdded(response){
	console.log("Image non ajoutée");
}

// -------------- AFFICHAGE DES INFORMATION DANS LA PAGE -------------- ///

function load_devis_info(devis){
	$("#info_devis > #date_devis").text("Date du devis : " + devis.date_devis);
	$("#info_devis > #montant_total").text("Montant total : " + devis.montant_total + " €");
	$("#info_devis > #duree_travaux").text("Durée des travaux : " + devis.duree_travaux + " jour(s)");
	$("#info_devis > #type_amenagement").text("Type d'aménagements : "+ devis.type_amenagement);
};

function load_travaux_info(devis){
	hide_all();
	$("#info_travaux").show();
	$("#info_travaux > #etat_courant").text("Etat courant : " + etat_to_string(devis.etat_des_amenagements));
	$("#info_travaux > #etat_courant").show();
	switch (devis.etat_des_amenagements) {
		case 'DI':
			$("#info_travaux > #date_debut_travaux_picker").show();
			$("#info_travaux > #confirmer_commande").show();
			$("#info_travaux > #annuler_commande").show();
			break;
		case 'CC':
			$("#info_travaux > #date_debut_travaux_picker").show();
			let date = new Date(devis.date_debut_travaux);
			let formatedDate = date.toISOString().substr(0,10);
			console.log(formatedDate);
			$("#date_travaux_input").val(formatedDate);
			$("#info_travaux > #confirmer_date_debut_travaux").show();
			$("#info_travaux > #annuler_commande").show();
			break;
		case 'DDTC':
			$("#info_travaux > #date_debut_travaux").text("Date début des travaux : " + devis.date_debut_travaux);
			$("#info_travaux > #date_debut_travaux").show()
			if(devis.duree_travaux >= 15){
				$("#info_travaux > #facture_milieu_envoye").show();
				$("#info_travaux > #facture_milieu_envoye").prop('disabled',false);
				$("#info_travaux > #facture_milieu_envoye_label").show();
			}
			else{
				$("#info_travaux > #facture_fin_envoye").show();
				$("#info_travaux > #facture_fin_envoye_label").show();
				$("#info_travaux > #facture_fin_envoye").prop('disabled',false);
			}

			$("#info_travaux > #confirmer_passer_etape_suivante").show();
			break;
		case 'MF':

			$("#info_travaux > #date_debut_travaux").text("Date début des travaux : " + devis.date_debut_travaux);
			$("#info_travaux > #date_debut_travaux").show()

			if(devis.duree_travaux >= 15){
				$("#info_travaux > #facture_milieu_envoye").show();
				$("#info_travaux > #facture_milieu_envoye").prop('disabled',true);
				$("#info_travaux > #facture_milieu_envoye").prop('checked',true);
				$("#info_travaux > #facture_milieu_envoye_label").show();
			}

			$("#info_travaux > #facture_fin_envoye").show();
			$("#info_travaux > #facture_fin_envoye_label").show();
			$("#info_travaux > #facture_fin_envoye").prop('disabled',false);

			$("#info_travaux > #confirmer_passer_etape_suivante").show();
			break;

		case 'FF':
			nbre_preview = 0;
			$("#image_apres_amenagement_preview").html("");

			$("#info_travaux > #date_debut_travaux").text("Date début des travaux : " + devis.date_debut_travaux);
			$("#info_travaux > #date_debut_travaux").show()

			if(devis.duree_travaux >= 15){
				$("#info_travaux > #facture_milieu_envoye").show();
				$("#info_travaux > #facture_milieu_envoye").prop('disabled',true);
				$("#info_travaux > #facture_milieu_envoye").prop('checked',true);
				$("#info_travaux > #facture_milieu_envoye_label").show();
			}

			$("#info_travaux > #facture_fin_envoye").show();
			$("#info_travaux > #facture_fin_envoye").prop('disabled',true);
			$("#info_travaux > #facture_fin_envoye").prop('checked',true);
			$("#info_travaux > #facture_fin_envoye_label").show();

			$("#info_travaux > #facture_fin_paye").show();
			$("#info_travaux > #facture_fin_paye").prop('disabled',false);
			$("#info_travaux > #facture_fin_paye_label").show();

			$("#info_travaux > #photo_apres_amenagement_insert").show();
			$("#ajout_image_apres_amenagement").val("");
			$("#info_travaux > #confirmer_passer_etape_suivante").show();
			break;
		case 'DDA':
			break;

		case 'RV':
			nbre_preview = 0;
			$("#image_apres_amenagement_preview").html("");

			$("#info_travaux > #date_debut_travaux").text("Date début des travaux : " + devis.date_debut_travaux);
			$("#info_travaux > #date_debut_travaux").show();

			$("#info_travaux > #facture_milieu_envoye").show();
			$("#info_travaux > #facture_milieu_envoye").prop('disabled',true);
			$("#info_travaux > #facture_milieu_envoye_label").show();

			$("#info_travaux > #facture_fin_envoye").show();
			$("#info_travaux > #facture_fin_envoye").prop('disabled',true);
			$("#info_travaux > #facture_fin_envoye_label").show();

			$("#info_travaux > #facture_fin_paye").show();
			$("#info_travaux > #facture_fin_paye").prop('disabled',true);
			$("#info_travaux > #facture_fin_paye_label").show();

			$("#info_travaux > #photo_apres_amenagement_insert").show();
			$("#ajout_image_apres_amenagement").val("");
			$("#info_travaux > #confirmer_changement").show();
			break;
	}


}

function load_client_info(client){
	$("#info_client > #nom").text("Nom : " + client.nom);
	$("#info_client > #prenom").text("Prenom : " + client.prenom);
	$("#info_client > #rue").text("Rue : " + client.rue);
	$("#info_client > #numero").text("Numero : " + client.numero);

	$("#info_client > #code_postal").text("Code postal : " + client.code_postal);
	$("#info_client > #ville").text("Ville : " + client.ville);
	$("#info_client > #email").text("Email : " + client.email);
	$("#info_client > #telephone").text("Téléphone : " + client.telephone);

	$("#titre_visualisation_devis").text("Visualisation devis de : " + client.prenom + " " + client.nom );

	if(client.boite != null){
			$("#info_client > #boite").text("Boite : " + client.boite);
	}else {
			$("#info_client > #boite").text("Boite : / ");
	}

};


// --------------- UTILS ------------------------------ //

function set_devis_etat(etat){
	let data = { id_devis : id_devis, etat_devis: etat  }
	putData(API_NAME_DEVIS , data , localStorage.getItem("token"), onEtatSet , onErrorEtatSet);
}

//Hide all in visualisation_devis
function hide_all(){
	$("#info_travaux > ").hide();
}

function add_image_preview(image_src,numero_image){
  $("#image_apres_amenagement_preview").append("<div class=\"images_preview_div\"> <img class=\"images_preview\" src=\""+ image_src +"\" >"
	 +"<select class=\"select_image_type\" id=\"select_type_image" + numero_image+ "\">"
	 +"<input class=\"radio_btn_image_pref\" type=\"radio\" name=\"photo_preferee\" value=\""+numero_image + "\">"
	 +"<input class=\"check_box_visible\" type=\"checkbox\" id=\"checkbox_image"+numero_image+"\">"
	 +"<label for=\"checkbox_image"+numero_image+"\"> Visible ?</label>"
	 +"</div>");
	var select = $("#select_type_image" + numero_image);
	$.each(all_type_amenagement,function(){
		select.append($("<option />").val(this.id_amenagement).text(this.nom))
	});
	align_photo_pref();
}


function etat_to_string(etat){
	switch (etat) {
		case 'DI':
				return "Devis introduit";

		case 'CC':
			return "Commande confirmée";

		case 'DDTC':
			return "Date de début de travaux confirmée";

		case 'MF':
			return "Facture de milieu de chantier envoyée";

		case 'FF':
			return "Facture de fin de chantier envoyée";

		case 'DDA':
			return "Demande d'aménagement annulée"

		case 'RV':
			return "Visible";

	}
}

function align_photo_pref(){
	let photo_pref_pos = $("#photo_preferee").position()
	if(photo_pref_pos != null){
		$('#icone_photo_pref').css({ 'position' : 'absolute', 'left': photo_pref_pos.left + 'px', 'top': photo_pref_pos.top + 'px' });
	}
}

function envoye_photo(){
	console.log("Envoie photo");
	$("#image_apres_amenagement_preview > .images_preview_div > img").each((index,element) => {
			let i = index + 1;
			let photo_pref = false;
			let typeAmenagement = $("#select_type_image"+i+" option:selected").val();

			let isVisible = $("#checkbox_image"+i).prop('checked');
			console.log("ISvisible"  + isVisible + "  " + $("#checkbox_image"+i));


			var radioValue = $("input[name='photo_preferee']:checked").val();
			if(Number(radioValue) === i){
				photo_pref = true;
			}
			//let date_photo = new Date()
			const data = {image:element.src,id_amenagement:typeAmenagement,id_devis:id_devis,token:localStorage.getItem("token"),preferee:photo_pref,visible:isVisible};
			postData(API_IMAGE,data,localStorage.getItem("token"),onImageAdded,onImageNotAdded);
	});
}

export {show_devis, show_devis_client};
