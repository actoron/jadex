# gnuplot script
set title "Evaluation of case study: mining ore resources"
set xlab "Number of sentries"
set ylab "Time in seconds"
#set zlab "y-Point"
#set yrange [-1:8]
#set xtics 10
set grid
#plot "data100.txt" using 0:(($1+$2+$3+$4+$5+$6+$7+$8+$9+$10)/10) w l title "Mean Average of chosen direction (10 runs of 100 vehicles)"
#plot "data200Strategy5CulTime.txt" using 1:2 w l title "Cumulated Travel Time with Deterministic Routing - Run1", "data200Strategy5CulTime.txt" using 3:4 #w l title "Cumulated Travel Time with Stochastic Routing - Run 2 ", "data200Strategy5CulTime.txt" using 5:6 w l title "Cumulated Travel Time with #Deterministic Routing - Run3", "data200Strategy5CulTime.txt" using 7:8 w l title "Cumulated Travel Time with Deterministic Routing - Run4"
#plot "data200Strategy5CulTime.txt" using 1:2 w l title "Mean Value", "data200Strategy6CulTime.txt" using 1:2 w l title "Median Value"

set xrange [0:18]
set yrange [0:120]
#set zrange [-0.2:1.1]
#set view 45,40,1.0,2.5
#splot 'data.txt'
plot "sentrydata.txt" using 1:2 w l title "Mean Value", "sentrydata.txt" using 1:3 w l title "Median Value"

pause -1
set output "SentryDataEval.ps"
#set terminal postscript eps enhanced color defaultplex "Arial" 14
set terminal postscript "Arial" 14
#set terminal postscript eps enhanced color
#set term postscript eps enhanced color


#set size 1.0, 0.6
#set terminal postscript portrait enhanced mono dashed lw 1 "Helvetica" 14 
#set output "my-plot_Run_21_with_100_vehicles.ps"
replot
set terminal x11
set size 1,1