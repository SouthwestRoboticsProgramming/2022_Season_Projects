var slider = document.getElementById("squirrels_slider");
var output = document.getElementById("num_squirrels")
output.innerHTML = slider.value; //Dispaly the default value

// Update the slider value when it is changed
slider.oninput = function() {
    output.innerHTML = this.value;
}

const hamburger = document.querySelector(".hamburger");
const navMenu = document.querySelector(".nav-menu");

hamburger.addEventListener("click",mobileMenu);

function mobileMenu() {
    hamburger.classList.toggle("active");
    navMenu.classList.toggle("active");
}