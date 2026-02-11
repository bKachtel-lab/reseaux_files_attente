set terminal pngcairo size 900,600
set output "densite_temps_presence.png"

set title "Densité du temps de présence"
set xlabel "Temps de présence (ms)"
set ylabel "Densité"


set grid

plot \
"reseau_lambda0.002_n1_p0.5_T50000.dat" using 3 smooth kdensity with lines title "1 serveur", \
"reseau_lambda0.002_n2_p0.5_T50000.dat" using 3 smooth kdensity with lines title "2 serveurs", \
"reseau_lambda0.002_n3_p0.5_T50000.dat" using 3 smooth kdensity with lines title "3 serveurs"
