Algorytm do kompresi huffmana jest uruchamiany komendą tak jak poniżej
z poziomu folderu AiSD2025Ex5

java -jar .\target\AiSD2025ZEx5-1.0-SNAPSHOT.jar 
-m <comp/decomp> 
-s <ścierzka do pliku którego chcemy skopresować> 
-d <ścirzka do miejsca w którym checmy mieć plik wyjściowy> 
-l <max długość łańcucha>

program jest odporny na nie podanie flagi -d i -l, które to jest wstanie obsłużyć automatycznie.
Podczas braku falgi -d algorytm przypisuje jako folder wyjściowy folder projektu. 
Kiedy urzytkownik poda błędną flagę -l, bądź jej nie poda wcale, algorytm przypiszę jej wartość 1.
W systuacji kiedy użytkownik nie poda -s, bądź zostawi -s "puste" program wypisze błąd na konsole,
podając powód dla którego przerwał swoje działanie.

Program jest wstanie obsługiwać pliki: zip, mp4, mp3, jpg, txt i może inne - tylko na tych był testowany. 

Główną wadą algorytmu jest moment w którym tekst jest nie powtarzalny - dużo różnych łańcuchów, i kiedy teskst 
jest bardzo krótki i różnorodny, a użytkownik my wybierzemy opcje z długim łańcuchem.
Wtedy samo zapisanie drzewa Huffmana do dekodowania znacznie obciąża plik.

Przykład: 

plik txt z zawartością: ABCDEFABCDEF, długość maksymalna łańcucha 2, 

dłguość drzwa zakodowanego w bitach: 14


długość kod z kompresowanego tekstu: 10 