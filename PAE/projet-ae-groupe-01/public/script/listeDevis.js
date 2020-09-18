"use strict"
import {getData} from "./ApiUtils.js";
import {show_devis_client} from "./visualisationDevis.js";
const API_NAME_TOKEN= "/utilisateur/verification";
const API_NAME_DEVIS="/listeDevis";
var typeUtilisateur="";
$(document).ready(function(){

		$("#menu_btn_mes_devis").click( e =>{
			$('body > :not(#header_accueil_client)').hide();
			getData(API_NAME_TOKEN,localStorage.getItem("token"),onGetVerification,onErrorVerification);
		});

});
function onErrorListeDevis(response){
    console.log("error affichage liste des devis pour un client ");
};

function onGetListeDevis(response){
	    afficherListeDevis(response);
};

function onGetVerification(response){
	typeUtilisateur=response.autorisation;
	if(typeUtilisateur=="C"){
		//on verifie que la personne connecte est bien un client
		getData(API_NAME_DEVIS,localStorage.getItem("token"),onGetListeDevis,onErrorListeDevis);
	};
};

function onErrorVerification(response){
	console.log("erreur reception verification du type de l'utilisateur ");
};

//fonction qui affiche les devis pour un client
function afficherListeDevis(response){
	console.log(response);
	 $('#devis_client').empty();
	 let tableDevis=response;
		let state="";
		let photoPrefereURL="";
		let dateDebutTravaux="";
		for(let i=0;i<tableDevis.length;i++){
			dateDebutTravaux=tableDevis[i].dateDebutTravaux;

			if(tableDevis[i].dateDebutTravaux===undefined){
				dateDebutTravaux="";
			}
			if(tableDevis[i].photoPrefere!=null){
				photoPrefereURL="<img src="+tableDevis[i].photoPrefere+" class='imageflottanteProfil' alt=...>";
			}
			else{
				photoPrefereURL="<img src=./image/image_default.png class='imageflottanteProfil' alt=...>";
			}

			switch(tableDevis[i].etatDesAmenagements) {
			   case 'DI':
			     state="Devis introduit";
			     break;
			   case 'CC':
				 state="Commande confirmée";
				 break;
			   case 'DDTC':
				 state="Date de début de travaux confirmé";
			     break;
			   case 'MF':
				 state="Facture de milieu de chantier envoyé";
				 break;
			   case 'FF':
				 state="Facture de fin de chantier envoyé";
				 break;
			   case 'DDA':
				 state="Demande d'aménagement annulé"
				 break;
			   default:
			     state="Visible";
			 };
				$('#devis_client').append(
					"<div id="+"devis_client_"+tableDevis[i].idDevis +" class=divRecherche>"
			       +photoPrefereURL
			       +"<p>type(s) d'aménagement(s):<span  class=divTypeAmenagement>"+tableDevis[i].typeAmenagement+"</span></p>"
			       +"<p>montant:"+tableDevis[i].montantTotal+"€"+"</p>"
			       +"<p>date:"+tableDevis[i].dateDevis+"</p>"
			       +"<p>durée des travaux:"+tableDevis[i].dureeTravaux+"jours"+"</p>"
			       +"<p>photo(s) avant aménagement(s):"+tableDevis[i].nombrePhotosAvant+"</p>"
			       +"<p>photo(s) après aménagement(s):"+tableDevis[i].nombrePhotosApres+"</p>"
			       +"<p>date des debut de travaux:"+tableDevis[i].dateDebutTravaux+"</p>"
			       +"<p>état des travaux: <span  class=divTypeAmenagement+>"+state+"</span></p>"
			       +"</div>"
				);

				$("#devis_client_"+tableDevis[i].idDevis).click(function(){
					let id = $(this).attr("id");
					id = id.split("_")[2];
					show_devis_client(tableDevis[i].idDevis);
				})

		}/*fin boucle du tableau*/
			$("#page_devis_client").show();
			$("#devis_client").show();

};
