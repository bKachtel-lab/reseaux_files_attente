set terminal pngcairo size 1000,600
set title "N(t) - nombre de clients dans le reseau"
set xlabel "Temps (ms)"
set ylabel "Nombre de requetes"
set grid

set output "courbes/nt_n2_0.002.png"
plot "nt_lambda0.002_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "blue" title "λ=0.002, n=1"

set output "courbes/nt_n2_0.008.png"
plot "nt_lambda0.008_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "red" title "λ=0.008, n=1"

set output "courbes/nt_n2_0.010.png"
plot "nt_lambda0.010_n2_p0.5_T100000.dat" using 1:2 with lines lw 2 lc rgb "green" title "λ=0.010, n=1"