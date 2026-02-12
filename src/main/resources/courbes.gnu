reset
set terminal pngcairo size 1200,600
set output "test_4_nt_n2_0.002.png"
set xlabel "Temps (ms)"
set ylabel "Nombre de requêtes"
set title "Convergence – Test 4 (n = 2, lambda = 0.002)"
set grid
set xrange [0:50000]
set xtics 0,5000,100000
set yrange [0:80]
set ytics 0,20,40,60,80
plot 'test_4_nt_lambda0.002_n2_p0.5_T100000.dat' using 1:2 with lines lw 2 lc rgb "blue" title "N(t)"
set output
