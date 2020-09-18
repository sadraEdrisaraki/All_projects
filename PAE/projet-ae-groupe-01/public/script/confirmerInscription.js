import { postData, getData, putData} from "./ApiUtils.js";
const API_NAME_USER = "/utilisateur/confirmation";
var tableauCharge = false;

$(document).ready(function(){

    $("#menu_btn_confirmer_inscription").click(e=>{
        $('body > :not(#header_accueil_ouvrier)').hide();
        $("#page_confirmer_inscription").show();
        
        console.log("Affichage tableau utilisateur");
        getData(API_NAME_USER , localStorage.getItem("token"), onRecuperationListeUtilisateur , onRecuperationListeUtilisateurError);

    })
})


/**
 * Génère un tableau d'utilisateur sur base de la réponse reçu
 * @param {*} response réponse reçu du serveur
 */
function genererTableauUtilisateur(response){
    var tableau = document.getElementById("tableau_utilisateur_confirmation");

    // Genere les headers (th)
    var rowHead = tableau.insertRow(0);
    var th = document.createElement('th');
    th.innerHTML = "Nom";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Prenom";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Pseudo";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Email";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Ville";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Date d'inscription";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Confirmation";
    rowHead.appendChild(th);

    for(let i = 0 ; i < response.length ; i++){
        var row = tableau.insertRow(i+1);
        // Nom
        var cell = row.insertCell(0);
        cell.innerHTML = response[i].nom;
        // Prenom
        var cell = row.insertCell(1);
        cell.innerHTML = response[i].prenom;
        // pseudo
        var cell = row.insertCell(2);
        cell.innerHTML = response[i].pseudo;
        // email
        var cell = row.insertCell(3);
        cell.innerHTML = response[i].email;
        // ville
        var cell = row.insertCell(4);
        cell.innerHTML = response[i].ville;
        // date inscription
        var cell = row.insertCell(5);

        cell.innerHTML = response[i].dateInscription;
        // bouton
        var cell = row.insertCell(6);
        var btn = document.createElement("button");
        btn.value = response[i].idUtilisateur;
        btn.innerText = "Confirmer Inscription";
        btn.className = "mini_btn_form";
        cell.appendChild(btn);
        // listener btn
        btn.addEventListener("click", e=> {
            e.preventDefault();
            const id = e.target.value;
            onConfirmationInscription(id);
        });        
    }
    
}

function onRecuperationListeUtilisateur(response){
    console.log(response);
    // Supprime le tableau pour le recréer (permet de recréer un tableau dynamique)
    if(tableauCharge){
        var tableau = document.getElementById("tableau_utilisateur_confirmation");
        tableau.innerHTML = '';
    }
    genererTableauUtilisateur(response);
    tableauCharge = true;
}

function onRecuperationListeUtilisateurError(response){
    console.log(response.message);
}

function onConfirmationInscription(id) {
    const data = {id_utilisateur:id};
    putData(API_NAME_USER, data, localStorage.getItem("token"), onConfirmer, onConfirmerError);
}

function onConfirmer(response) {
    console.log("la confirmation a bien eu lieu");
    getData(API_NAME_USER , localStorage.getItem("token"), onRecuperationListeUtilisateur , onRecuperationListeUtilisateurError);
}

function onConfirmerError(response) {
    console.log("la confirmation n'a pas été faite : " + response.message);
}