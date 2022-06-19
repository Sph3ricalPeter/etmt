## Exam Test Management Tool

Exam Test Management Tool (ETMT in short) simplifies the proces of creating exam tests in general, with a primary focus on [Moodle](https://moodle.org/).

ETMT is a standalone web application deployed in Docker and allows:

### 1. Creating a library of questions
  * short answer, single-choice and multiple choice questions are currently supported
### 2. Importing categories and questions from Moodle via XML
### 3. Creating exam test templates
Exam test templates consist of topics which specify:
  *  number of questions from a given category
  *  number of questions with a cartain amount of points as a reward
  *  a combination of the two
### 4. generating exam test variants using different strategies
   *  UNIQUE strategy creates completely different test variants (with no overlap of questions)
   *  COMPROMISE strategy uses AETG system from Pairwise testing to generate combinations with all pairs of questions, reducing the overlap of questions between test variants
### 5. exporting generated test variants 
   *  to XML format for import in Moodle
   *  to PDF for print

## Usage
- `Docker Desktop 4.7.1` or newer is required
- app can be started by running `docker compose -f "docker-compose.yml" up -d --build` in the `etmt` root folder
- GUI can be found at http://localhost:3001

# Links
- [App Diagrams](https://drive.google.com/file/d/1jXgTX40HRlTDFJGnaNFyr2LOSK9yCICd/view?usp=sharing)
- [Original repo](https://gitlab.fel.cvut.cz/jezekpe6/sem_pro)