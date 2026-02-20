set terminal pngcairo size 1200,800
set output "courbes/comparaison_λ_p0.8.png"
set title "Comparaison de N(t) pour différentes valeurs de lambda (n = 3) avec P = 0.5"
set xlabel "Temps (ms)"
set ylabel "Nombre de requêtes"

set grid
set key top left

set style line 1 lc rgb "blue" lw 2 lt 1
set style line 2 lc rgb "red" lw 2 lt 2
set style line 3 lc rgb "green" lw 2 lt 3

plot "nt_lambda0.002_n3_p0.8_T100000.dat" using 1:2 with lines ls 1 title "lambda = 0.002 (faible)", \
     "nt_lambda0.008_n3_p0.8_T100000.dat" using 1:2 with lines ls 2 title "lambda = 0.008 (moyen)", \
     "nt_lambda0.010_n3_p0.8_T100000.dat" using 1:2 with lines ls 3 title "lambda = 0.010 (élevé)"