set terminal pngcairo size 1000,600
set output "nt.png"
set title "N(t) - nombre de clients dans le reseau"
set xlabel "Temps (ms)"
set ylabel "N(t)"
set grid
plot "nt_lambda0.002_n1_p0.5_T100000.dat" using 1:2 with steps title "N(t)"