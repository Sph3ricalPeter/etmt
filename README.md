## Nástroj pro zadávání zkouškových testů

Vedoucí: Ing. Karel Frajták, Ph.D.

### Popis

Nástroj pomůže při organizaci zkouškových testů. Vyučující bude moci zadávat otázky (volný text, jedna odpověď je
správna, více odpovědí je správných, apod.), seskupovat je dle definované kategorie, označovat štítky, definovat počet
bodů za správně zodpovězenou otázku.
Nástroj sestaví test dle zadaného celkového počtu bodů za všechny otázky, celkového počtu otázek, kategorií a počtu
otázek z každé kategorie.
Systém zaručí minimální opakování v testech zadávaných v jednom zkouškovém období. Nástroj upozorní obsluhu na
nutnost doplnění otázek do zásobníku otázek v případě, že nelze další varianty testu sestavit.
Test bude pak možné vyexportovat do formátu pro import do Moodle nebo do PDF pro přímý tisk.
Vytvořený nástroj vhodným způsobem otestujte.

### Výstupy
- práce ve formátu PDF, LaTeX zdroj i vygenerované PDF je umístěno ve složce `/thesis`
- webová aplikace umožňující tvorbu zkouškových testů pro nahrání do moodle, je tvořena:
  - `/etmt-be` - serverová aplikace, Java, Maven
  - `/etmt-fe` - klientská aplikace, React.js
  - `/fa-pdflatex` - PdfLatex služba, FastAPI

## Návod ke spuštění aplikace
- je třeba mít nainstalovaný `Docker Desktop 4.7.1` či novější
- aplikace se sestaví příkazem `docker compose -f "docker-compose.yml" up -d --build` v kořenovém adresáři
- během několika minut by na http://localhost:3001 měla běžet aplikace - obrazovka Login (při prvním spuštění může načítání trvat déle)

# Rozcestník
- [Závěrečná zpráva semestrálního projektu v LaTeX Overleaf](https://www.overleaf.com/read/hgphgsrqpzkc)
- [Gitlab repozitář aplikace](https://gitlab.fel.cvut.cz/jezekpe6/sem_pro)
- [DrawIO diagramy](https://drive.google.com/file/d/1jXgTX40HRlTDFJGnaNFyr2LOSK9yCICd/view?usp=sharing)
