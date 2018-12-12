package jadex.micro.examples.eliza;
import java.util.Vector;

/**
Eliza in Java.
Adapted from a BASIC program I found floating on the net.
Eliza was originally written by Joseph Weizenbaum.
This version is an adaption of the program
as it appeared in the memorable magazine Create Computing 
around 1981.
<br>
Jesper Juul - jj@pobox.com.
Copenhagen, February 24th, 1999.
*/

public class ElizaParse
{

public ElizaParse()
{
init();
}

private static String intromsg[]=
{
"**************************",
"ELIZA",
"CREATIVE COMPUTING",
"MORRISTOWN, NEW JERSEY",
"",
"ADAPTED FOR IBM PC BY",
"PATRICIA DANIELSON AND PAUL HASHFIELD",
//"PLEASE DON'T USE COMMAS OR PERIODS IN YOUR INPUTS",
"",
"Java version February 24th, 1999",
"By Jesper Juul - jj@pobox.com.","",
"**************************",
};

String[] getIntroMsg()
{
return intromsg;
}

/**
Vector holding strings that have been added using PRINT.
*/
public Vector<String> msg=new Vector<String>();

/**
Cute hack to make it look like BASIC.
This adds a String to the msg Vector.
*/
public void PRINT(String s)
{
msg.addElement(s);
}

int N1=36;//,N2=14,N3=112;
int WORDINOUT=7;

int S[]=new int[N1+1];
int R[]=new int[N1+1];
int N[]=new int[N1+1];
String WORDIN[]=new String[WORDINOUT];
String WORDOUT[]=new String[WORDINOUT];


void init()
{
int i;

for (i=0;i<WORDINOUT;i++)
{
 WORDIN[i]=wordinout[i][0];
 WORDOUT[i]=wordinout[i][1];
}

for (int X=1;X<=N1;X++)
{
 S[X]=rightreplies[X*2-2];
 int L=rightreplies[X*2-1];
 R[X]=S[X];
 N[X]=S[X]+L-1;
}



PRINT("Hi! I'm the famous Eliza program. What's your problem?");

}


public boolean exit=false;

public void handleLine(String I)
{
I="  "+I.toLowerCase()+"  ";

//Remove apostrophes = Line 210-210
I=removeChar(I,'\'');

if (I.indexOf("shut")>=0)
{
PRINT ("O.K. If you feel that way I'll shut up...");
 exit=true;
 return;
}

if (I.equals(lastline))
{
PRINT ("Please don't repeat yourself!");
return;
}

lastline=I;

int pos=0;
String C="error";
int K;
String F="";
boolean found=false;

for (K=0;K<N1;K++)
{
pos=I.indexOf(KEYWORD[K]);
if (pos>=0)
{
if (K==13)
{
//Should use regionmatches
if (I.indexOf(KEYWORD[29])>=0) K=29;
}
F=KEYWORD[K];
found=true;
break;
}
}

if (found)
{
C=I.substring(pos+F.length()-1);

//Swap strings = Line 430-560
for (int i=0;i<WORDINOUT;i++)
{
 C=replaceString(C,WORDIN[i],WORDOUT[i]);
}
//Remove extra spaces
 C=replaceString(C,"  "," ");
}
else
{
 K=35;
}

//600 F$ = REPLIES$(R(K))
F=REPLIES[R[K+1]];

//610 R(K)=R(K)+1:IF R(K)>N(K) THEN R(K)=S(K)
if (++R[K+1]>N[K+1]) R[K+1]=S[K+1];

//620 IF RIGHT$(F$,1)<>"*" THEN PRINT F$:P$=I$:GOTO 170
if (F.charAt(F.length()-1)!='*')
{
 PRINT (F);
}
else
{
//625 IF C$<>"   " THEN 630
 if (C.equals("   "))
 {
//626 PRINT "YOU WILL HAVE TO ELABORATE MORE FOR ME TO HELP YOU"
PRINT ("You will have to elaborate more fore me to help you");
//627 GOTO 170
 }
 else
 {
//630 PRINT LEFT$(F$,LEN(F$)-1);C$
//640 P$=I$:GOTO 170
  PRINT(F.substring(0,F.length()-1)+C);
 }
}
}

/**
Remembers the last line typed.
*/
String lastline="-";

/**
Line 1000
*/
static String KEYWORD[]=
{ "can you ","can i ","you are ","you're ","i don't ","i feel ",
 "why don't you ","why can't i ","are you ","i can't ","i am ","i'm ",
 "you ","i want ","what ","how ","who ","where ","when ","why ",
 "name ","cause ","sorry ","dream ","hello ","hi ","maybe ",
 "no ","you ","always ","think ","alike ","yes ","friend ",
 "computer", "nokeyfound"};

/**
Line 1200
*/
static String wordinout[][]=
{{" are "," am "},{" were "," was "},{" you "," I "},{" your "," my "},
{" i've "," you've "},{" i'm "," you're "},
{" me "," you "}};

/**
Line 1300
*/
static String REPLIES[]=
{"Don't you believe that I can*",
	"Perhaps you would like to be like me*",
	"You want me to be able to*",
	"Perhaps you don't want to*",
	"Do you want to be able to*",
	"What makes you think I am*",
	"Does it please you to believe I am*",
	"Perhaps you would like to be*",
	"Do you sometimes wish you were*",
	"Don't you really*",
	"Why don't you*",
	"Do you wish to be able to*",
	"Does that trouble you*",
	"Do you often feel*",
	"Do you often feel*",
	"Do you enjoy feeling*",
	"Do you really believe I don't*",
	"Perhaps in good time I will*",
	"Do you want me to*",
	"Do you think you should be able to*",
	"Why can't you*",
	"Why are you interested in whether or not I am*",
	"Would you prefer if I were not*",
	"Perhaps in your fantasies I am*",
	"How do you know you can't*",
	"Have you tried?",
	"Perhaps you can now*",
	"Did you come to me because you are*",
	"How long have you been*",
	"Do you believe it is normal to be*",
	"Do you enjoy being*",
	"We were discussing you--not me.",
	"Oh, I*",
	"You're not really talking about me, are you?",
	"What would it mean to you if you got*",
	"Why do you want*",
	"Suppose you soon got*",
	"What if you never got*",
	"I sometimes also want*",
	"Why do you ask?",
	"Does that question interest you?",
	"What answer would please you the most?",
	"What do you think?",
	"Are such questions on your mind often?",
	"What is it that you really want to know?",
	"Have you asked anyone else?",
	"Have you asked such questions before?",
	"What else comes to mind when you ask that?",
	"Names don't interest me.",
	"I don't care about names --please go on.",
	"Is that the real reason?",
	"Don't any other reasons come to mind?",
	"Does that reason explain anything else?",
	"What other reasons might there be?",
	"Please don't apologize!",
	"Apologies are not necessary.",
	"What feelings do you have when you apologize?",
	"Don't be so defensive!",
	"What does that dream suggest to you?",
	"Do you dream often?",
	"What persons appear in your dreams?",
	"Are you disturbed by your dreams?",
	"How do you do ...please state your problem.",
	"You don't seem quite certain.",
	"Why the uncertain tone?",
	"Can't you be more positive?",
	"You aren't sure?",
	"Don't you know?",
	"Are you saying no just to be negative?",
	"You are being a bit negative.",
	"Why not?",
	"Are you sure?",
	"Why no?",
	"Why are you concerned about my*",
	"What about your own*",
	"Can you think of a specific example?",
	"When?",
	"What are you thinking of?",
	"Really, always?",
	 "Do you really think so?",
	 "But you are not sure you*",
	 "Do you doubt you*",
	 "In what way?",
	 "What resemblance do you see?",
	 "What does the similarity suggest to you?",
	 "What other connections do you see?",
	 "Could there really be some connection?",
	 "How?",
	 "You seem quite positive.",
	 "Are you sure?",
	 "I see.",
	 "I understand.",
	 "Why do you bring up the topic of friends?",
	 "Do your friends worry you?",
	 "Do your friends pick on you?",
	 "Are you sure you have any friends?",
	 "Do you impose on your friends?",
	 "Perhaps your love for friends worries you.",
	 "Do computers worry you?",
	 "Are you talking about me in particular?",
	 "Are you frightened by machines?",
	 "Why do you mention computers?",
	 "What do you think machines have to do with your problem?",
	 "Don't you think computers can help people?",
	 "What is it about machines that worries you?",
	 "Say, do you have any psychological problems?",
	"What does that suggest to you?",
	 "I see.",
	  "I'm not sure I understand you fully.",
	  "Come come elucidate your thoughts.",
	  "Can you elaborate on that?",
	  "That is quite interesting.",
};

/**
Line 2500. These are the mysterious numbers that keep
track of which replies have been used and so one.
Clever but hard to read.
*/
final static int rightreplies[]=
{
 1,3,4,2,6,4,6,4,10,4,14,3,17,3,20,2,22,3,25,3,
 28,4,28,4,32,3,35,5,40,9,40,9,40,9,40,9,40,9,40,9,
 49,2,51,4,55,4,59,4,63,1,63,1,64,5,69,5,74,2,76,4,
80,3,83,7,90,3,93,6,99,7,106,6};


/**
Utility function that removes a char from a String.
*/
public static String removeChar(String s,char c)
{
if (s==null) return s;
int p;
while((p=s.indexOf(c))>=0)
{
s=s.substring(0,p-1)+s.substring(p+1);
}
return s;
}

/**
Utility function that replaces all occurences of a specific string
with another string.
*/
public static String replaceString(String s,String oldstring,String newstring)
{
int pos;
while ((pos=s.indexOf(oldstring))>=0)
{
 s=s.substring(0,pos)+newstring+s.substring(pos+oldstring.length());
}
return s;
}
}

