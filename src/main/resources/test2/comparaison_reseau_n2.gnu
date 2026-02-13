set terminal pngcairo size 1200,800 enhanced
set output "courbes/comparaison_reseau_n2.png"
set title "Comparaison des mesures réseau pour différentes valeurs de λ (n=1)"
set xlabel "Temps (ms)"
set ylabel "Valeur mesurée"
set grid
set key top left

plot "reseau_lambda0.002_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "blue" title "λ = 0.002", \
     "reseau_lambda0.008_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "red" title "λ = 0.008", \
     "reseau_lambda0.010_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "green" title "λ = 0.010"