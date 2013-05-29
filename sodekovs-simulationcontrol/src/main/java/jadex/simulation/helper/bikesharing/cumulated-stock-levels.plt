# gnuplot script
set title "Evaluation of case study: Bike Sharing - Stock Level Evaluation"
set xlab "Timeslot"
set ylab "Available Bike"
set grid
set xrange [0:1380]
set yrange [0:1]
plot "cumulated-stock-levels.txt" using 1:2 w l linecolor rgb "blue" title "Blue", "cumulated-stock-levels.txt" using 1:3 w l linecolor rgb "green" title "Green", "cumulated-stock-levels.txt" using 1:4 w l linecolor rgb "red" title "Red"

pause -1
set output "Bikesharing-Eval.ps"
set terminal postscript "Arial" 14 color solid
replot
set terminal x11
set size 1,1