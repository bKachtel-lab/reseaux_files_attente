set terminal pngcairo size 900,600
set output "hist_temps_presence.png"

set title "Histogramme du temps de présence"
set xlabel "Temps de présence (ms)"
set ylabel "Fréquence"

binwidth = 20
bin(x,width) = width * floor(x/width)

set boxwidth binwidth
set style fill solid 0.5
set grid ytics

plot "reseau_lambda0.0_n1_p0.5_T30000.dat" \
     using (bin($3,binwidth)):(1.0) smooth freq with boxes notitle
