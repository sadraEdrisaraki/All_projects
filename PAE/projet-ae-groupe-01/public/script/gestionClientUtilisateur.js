import { postData, getData, putData} from "./ApiUtils.js";
const API_NAME_CLIENT = "/client/clientSansUser";
const API_NAME_UTILISATEUR = "/utilisateur";
const API_NAME_LIER = "/utilisateur/lier";
const API_NAME_DEFINIR_OUVRIER = "/utilisateur";
var tableauCharge = false;
const COLOR_VALUE = "#d4e3e5";
var utilisateurMail = null;
var clientMail = null;

$(document).ready(function(){

    $("#menu_btn_administrer_utilisateur").click(e=>{
        $('body > :not(#header_accueil_ouvrier)').hide();
        $("#page_administrer_utilisateur").show();

        getData(API_NAME_CLIENT , localStorage.getItem("token") , onRecuperationListeClient , onRecuperationListeClientError);
        getData(API_NAME_UTILISATEUR , localStorage.getItem("token") , onRecuperationListeUtilisateur , onRecuperationListeUtilisateurError);
    })


    // Lie les deux comptes
    $("#btn_lier_utilisateur_client").click(e=>{
        if(utilisateurMail == null || clientMail == null){
            $("#msg_admin_utilisateur_error").css("color" , "red");
            $("#msg_admin_utilisateur_error").show();
            $("#msg_admin_utilisateur_error").text("Veuillez sélectionner un compte client et un compte utilisateur !");
        }
        else{
            const data = {email_client:clientMail , email_utilisateur:utilisateurMail};
            postData(API_NAME_LIER , data , localStorage.getItem("token") ,onLierClientUtilisateur , onLierClientUtilisateurError);
            utilisateurMail = null;
            clientMail = null;
        }
    })

    $("#btn_definir_ouvrier").click(e=>{
        if(utilisateurMail == null){
            $("#msg_admin_utilisateur_error").css("color" , "red");
            $("#msg_admin_utilisateur_error").show();
            $("#msg_admin_utilisateur_error").text("Veuillez sélectionner un compte utilisateur !");
        }
        else{
            const data = {email_utilisateur:utilisateurMail};
            putData(API_NAME_DEFINIR_OUVRIER , data , localStorage.getItem("token") ,onDefinirOuvrier , onDefinirOuvrierError);
            utilisateurMail = null;
        }
    })

    $("#tableau_utilisateur").click(e=>{
        console.log("utilisateur : " + e.target.parentNode.children[3].firstChild.data);
        utilisateurMail = e.target.parentNode.children[3].firstChild.data;
        cacherMessageErreur();
    })
    $("#tableau_client").click(e=>{
        console.log("client : " + e.target.parentNode.children[3].firstChild.data);
        clientMail = e.target.parentNode.children[3].firstChild.data;
        cacherMessageErreur();
    })

});

function genererTableauClient(response){
    var tableau = document.getElementById("tableau_client");

    // Genere les headers (th)
    var rowHead = tableau.insertRow(0);
    var th = document.createElement('th');
    th.innerHTML = "Nom";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Prenom";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Telephone";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Email";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Ville";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Code Postal";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Rue";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "Numero";
    rowHead.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = "N° Boite";
    rowHead.appendChild(th);

    


    for(let i = 0 ; i < response.length ; i++){
        var row = tableau.insertRow(i+1);

        // Nom
        var cell = row.insertCell(0);
        cell.innerHTML = response[i].nom;
        // Prenom
        var cell = row.insertCell(1);
        cell.innerHTML = response[i].prenom;
        // telephone
        var cell = row.insertCell(2);
        cell.innerHTML = response[i].telephone;
        // email
        var cell = row.insertCell(3);
        cell.innerHTML = response[i].email;
        // ville
        var cell = row.insertCell(4);
        cell.innerHTML = response[i].ville;
        // codePostal
        var cell = row.insertCell(5);
        cell.innerHTML = response[i].codePostal;
        // rue
        var cell = row.insertCell(6);
        cell.innerHTML = response[i].rue;
        // numero
        var cell = row.insertCell(7);
        cell.innerHTML = response[i].numero;
        // boite
        var cell = row.insertCell(8);
        if(response[i].boite == null){
            cell.innerHTML = "non défini";
            cell.style.color = "grey";
        }else{
            cell.innerHTML = response[i].boite;
        }
    }
    $(tableau).delegate("tr" , "click" , function(e){
        for(let i = 0 ; i < e.target.parentNode.children.length ; i++){
            $(e.target.parentNode.parentNode.children[i]).css("background-color" , COLOR_VALUE);
        }
        $(e.target.parentNode).css("background-color" , "#e38174");
    })
}

function genererTableauUtilisateur(response){
    var tableau = document.getElementById("tableau_utilisateur");

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
        
    }
$(tableau).delegate("tr" , "click" , function(e){
    for(let i = 0 ; i < e.target.parentNode.children.length ; i++){
        $(e.target.parentNode.parentNode.children[i]).css("background-color" , COLOR_VALUE);
    }
    $(e.target.parentNode).css("background-color" , "#e38174");
})
}


function onRecuperationListeClient(response){
    console.log(response);
    // Supprime le tableau pour le recréer (permet de recréer un tableau dynamique)
    if(tableauCharge){
        var tableau = document.getElementById("tableau_client");
        tableau.innerHTML = '';
    }
    genererTableauClient(response);
    tableauCharge = true;
}

function onRecuperationListeClientError(response){
    console.log(response.message);
}

function onRecuperationListeUtilisateur(response){
    console.log(response);
    // Supprime le tableau pour le recréer (permet de recréer un tableau dynamique)
    if(tableauCharge){
        var tableau = document.getElementById("tableau_utilisateur");
        tableau.innerHTML = '';
    }
    genererTableauUtilisateur(response);
    tableauCharge = true;
}

function onRecuperationListeUtilisateurError(response){
    console.log(response.message);
}

function cacherMessageErreur(){
    $("#msg_admin_utilisateur_error").hide();
}



// LIER CLIENT METHODE 

function onLierClientUtilisateur(response){
    console.log("la liaison a été un succès !");
    getData(API_NAME_CLIENT , localStorage.getItem("token") , onRecuperationListeClient , onRecuperationListeClientError);
    getData(API_NAME_UTILISATEUR , localStorage.getItem("token") , onRecuperationListeUtilisateur , onRecuperationListeUtilisateurError);

}

function onLierClientUtilisateurError(response){
    console.log("Erreur de liaison de compte : " + response.message)
}

function onDefinirOuvrier(response){
    console.log("l'ouvrier a été défini ave succès");
    getData(API_NAME_CLIENT , localStorage.getItem("token") , onRecuperationListeClient , onRecuperationListeClientError);
    getData(API_NAME_UTILISATEUR , localStorage.getItem("token") , onRecuperationListeUtilisateur , onRecuperationListeUtilisateurError);
}

function onDefinirOuvrierError(response){
    console.log("l'ouvrier n'a pas pu être défini : " + response.message);
}