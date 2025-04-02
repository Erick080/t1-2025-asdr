// Eduardo Enes Traunig
// Erick Branquinho Machado

import java.io.*;

public class AsdrSample {

   private static final int BASE_TOKEN_NUM = 301;

   public static final int IDENT = 302;
   public static final int INT = 303;
   public static final int DOUBLE = 304;
   public static final int BOOLEAN = 305;
   public static final int FUNC = 306;
   public static final int NUM = 307;
   public static final int VOID = 308;
   public static final int WHILE = 309;
   public static final int IF = 310;
   public static final int ELSE = 311;

   public static final String tokenList[] = { 
         "IDENT",
         "INT",
         "DOUBLE",
         "BOOLEAN",
         "FUNC",
         "NUM",
         "VOID",
         "WHILE",
         "IF",
         "ELSE" };

   /* referencia ao objeto Scanner gerado pelo JFLEX */
   private Yylex lexer;

   public ParserVal yylval;

   private static int laToken;
   private boolean debug;

   /* construtor da classe */
   public AsdrSample(Reader r) {
      lexer = new Yylex(r, this);
   }

   // GRAMATICA
   /* Prog -->  ListaDecl

   ListaDecl -->  DeclVar  ListaDecl
               |  DeclFun  ListaDecl
               |   vazio 
 
   DeclVar --> Tipo ListaIdent ';' DeclVar
             | vazio 
 
   Tipo --> int | double | boolean
 
   ListaIdent --> IDENT , ListaIdent  
                | IDENT      
 
   DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
             |  vazio 
 
   TipoOuVoid --> Tipo | VOID
 
   FormalPar -> paramList |  vazio 
 
   paramList --> Tipo IDENT , ParamList
               | Tipo IDENT 
 
   Bloco --> { ListaCmd }
 
   ListaCmd --> Cmd ListaCmd
     |   vazio 
 
   Cmd --> Bloco
       | while ( E ) Cmd
       | IDENT = E ;
       | if ( E ) Cmd RestoIf
 
   RestoIf -> else Cmd
         |     vazio 

   AQUI FOI FEITA A FATORACAO DE E  T

   E --> T EResto             
 
   EResto --> + T EResto
           | - T EResto
           | vazio

   T --> F TResto  
       
   TResto --> * F TResto
           | / F TResto
           | vazio

   F -->  IDENT
       | NUM
       | ( E ) */

   private void Prog() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || laToken == FUNC) {
         if (debug)
            System.out.println("Prog --> ListaDecl");        
         ListaDecl();
      } else
         yyerror("esperado tipo ou 'func'");
   }

   private void ListaDecl() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         if (debug)
            System.out.println("ListaDecl --> DeclVar ListaDecl");
         DeclVar();
         ListaDecl();
      } else if (laToken == FUNC) {
         if (debug)
            System.out.println("ListaDecl --> DeclFun ListaDecl");
         DeclFun();
         ListaDecl();
      } else {
         if (debug)
            System.out.println("ListaDecl -->  (*vazio*)  ");
      }
   }

   private void DeclVar() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         if (debug)
            System.out.println("DeclVar --> Tipo ListaIdent ; DeclVar");
         Tipo();
         ListaIdent();
         verifica(';');
         DeclVar();
      } else {
         if (debug)
            System.out.println("DeclVar -->  (*vazio*)  ");
      }
   }

   private void DeclFun() {
      if (laToken == FUNC) {
         if (debug)
            System.out.println("DeclFun --> FUNC TipoOuVoid IDENT ( FormalPar ) { DeclVar ListaCmd } DeclFun");
         verifica(FUNC);
         TipoOuVoid();
         verifica(IDENT);
         verifica('(');
         FormalPar();
         verifica(')');
         verifica('{');
         DeclVar();
         ListaCmd();
         verifica('}');
         DeclFun();
      } else {
         if (debug)
            System.out.println("DeclFun -->  (*vazio*)  ");
      }
   }

   private void ListaIdent(){
      verifica(IDENT);
      if (laToken == ',') {
         if (debug)
            System.out.println("ListaIdent --> IDENT , ListaIdent");
         verifica(',');
         ListaIdent();
      } else {
         if (debug)
            System.out.println("ListaIdent -->  IDENT  ");
      }
   }

   private void TipoOuVoid(){
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN){
         if (debug)
            System.out.println("TipoOuVoid --> Tipo");
         Tipo();
      }
      else if (laToken == VOID){
         if (debug)
            System.out.println("TipoOuVoid --> VOID");
         verifica(VOID);
      }
      else{
         yyerror("Esperava tipo ou void");
      }
   }

   private void Tipo(){
      if (laToken == INT){
         if (debug)
            System.out.println("Tipo --> INT");
         verifica(INT);
      }
      else if(laToken == DOUBLE){
         if (debug)
            System.out.println("Tipo --> DOUBLE");
         verifica(DOUBLE);
      }
      else if(laToken == BOOLEAN){
         if (debug)
            System.out.println("Tipo --> BOOLEAN");
         verifica(BOOLEAN);
      }
   }

   private void FormalPar(){
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN){
         if (debug)
            System.out.println("FormalPar --> ParamList");
         ParamList();
      }
      else{
         if (debug)
            System.out.println("FormalPar --> (*vazio*)");
      }
   }

   private void ParamList(){
      Tipo();
      verifica(IDENT);
      if (laToken == ','){
         if (debug)
            System.out.println("ParamList --> Tipo IDENT , ParamList");
         verifica(',');
         ParamList();
      }
      else{
         if (debug)
            System.out.println("ParamList --> Tipo IDENT");
      }
   }

   private void Bloco(){
      verifica('{');
      ListaCmd();
      verifica('}');
   }

   private void ListaCmd(){
      if (laToken == '{' || laToken == WHILE || laToken == IF || laToken == IDENT) {
         if (debug)
            System.out.println("ListaCmd --> Cmd ListaCmd");
         Cmd();
         ListaCmd();
      } else {
         if (debug)
            System.out.println("ListaCmd -->  (*vazio*)  ");
      }
   }

   private void Cmd() {
      if (laToken == '{') {
         if (debug)
            System.out.println("Cmd --> Bloco");
         Bloco();
      } else if (laToken == WHILE) {
         if (debug)
            System.out.println("Cmd --> while ( E ) Cmd");
         verifica(WHILE);
         verifica('(');
         E();
         verifica(')');
         Cmd();
      } else if (laToken == IDENT) {
         if (debug)
            System.out.println("Cmd --> IDENT = E ;");
         verifica(IDENT);
         verifica('=');
         E();
         verifica(';');
      } else if (laToken == IF) {
         if (debug)
            System.out.println("Cmd --> if ( E ) Cmd RestoIf");
         verifica(IF);
         verifica('(');
         E();
         verifica(')');
         Cmd();
         RestoIf();
      } else {
         yyerror("Esperava bloco, while, identificador ou 'if'.");
      }
   }

   private void RestoIf() {
      if (laToken == ELSE) {
         if (debug)
            System.out.println("RestoIf --> else Cmd");
         verifica(ELSE);
         Cmd();
      } else {
         if (debug)
            System.out.println("RestoIf -->  (*vazio*)  ");
      }
   }

   private void E() {
      if (debug)
         System.out.println("E --> T EResto");
      T();
      EResto();
   }

   private void EResto(){
      if (laToken == '+') {
         if (debug)
            System.out.println("EResto --> + T EResto");
         verifica('+');
         T();
         EResto();
      } else if (laToken == '-') {
         if (debug)
            System.out.println("EResto --> - T EResto");
         verifica('-');
         T();
         EResto();
      } else {
         if (debug)
            System.out.println("EResto -->  (*vazio*)  ");
      }
   }

   private void T() {
      if (debug)
         System.out.println("T --> F TResto");
      F();
      TResto();
   }

   private void TResto(){
      if (laToken == '*') {
         if (debug)
            System.out.println("TResto --> * F TResto");
         verifica('*');
         F();
         TResto();
      } else if (laToken == '/') {
         if (debug)
            System.out.println("TResto --> / F TResto");
         verifica('/');
         F();
         TResto();
      } else {
         if (debug)
            System.out.println("TResto -->  (*vazio*)  ");
      }
   }

   private void F(){
      if (laToken == IDENT) {
         if (debug)
            System.out.println("F --> IDENT");
         verifica(IDENT);
      } else if (laToken == NUM) {
         if (debug)
            System.out.println("F --> NUM");
         verifica(NUM);
      } else if (laToken == '(') {
         if (debug)
            System.out.println("F --> ( E )");
         verifica('(');
         E();
         verifica(')');
      } else {
         yyerror("Esperava identificador, numero ou '(E)'.");
      }
   }

   private void verifica(int expected) {
      if (laToken == expected) {
         laToken = this.yylex();
      }
      else {
         String expStr, laStr;

         expStr = ((expected < BASE_TOKEN_NUM)
               ? "" + (char) expected
               : tokenList[expected - BASE_TOKEN_NUM]);

         laStr = ((laToken < BASE_TOKEN_NUM)
               ? Character.toString(laToken)
               : tokenList[laToken - BASE_TOKEN_NUM]);

         yyerror("esperado token: " + expStr +
               " na entrada: " + laStr);
      }
   }

   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
      int retVal = -1;
      try {
         yylval = new ParserVal(0); // zera o valor do token
         retVal = lexer.yylex(); // le a entrada do arquivo e retorna um token
      } catch (IOException e) {
         System.err.println("IO Error:" + e);
      }
      return retVal; // retorna o token para o Parser
   }

   /* metodo de manipulacao de erros de sintaxe */
   public void yyerror(String error) {
      System.err.println("Erro: " + error);
      System.err.println("Entrada rejeitada");
      System.out.println("\n\nFalhou!!!");
      System.exit(1);
   }

   public void setDebug(boolean trace) {
      debug = trace;
   }

   /**
    * Runs the scanner on input files.
    *
    * This main method is the debugging routine for the scanner.
    * It prints debugging information about each returned token to
    * System.out until the end of file is reached, or an error occured.
    *
    * @param args the command line, contains the filenames to run
    *             the scanner on.
    */
   public static void main(String[] args) {
      AsdrSample parser = null;
      try {
         if (args.length == 0)
            parser = new AsdrSample(new InputStreamReader(System.in));
         else
            parser = new AsdrSample(new java.io.FileReader(args[0]));

         parser.setDebug(true);
         laToken = parser.yylex();

         parser.Prog();

         if (laToken == Yylex.YYEOF)
            System.out.println("\n\nSucesso!");
         else
            System.out.println("\n\nFalhou - esperado EOF.");

      } catch (java.io.FileNotFoundException e) {
         System.out.println("File not found : \"" + args[0] + "\"");
      }
   }

}
