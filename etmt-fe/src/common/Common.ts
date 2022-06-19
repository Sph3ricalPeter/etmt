export const SUCCESS_MSG = "Yay!";
export const ERR_MSG = "Ooops!";

export type Nullable<T> = T | null;

export enum QuestionType {
  ShortAnswer = "shortAnswer",
  SingleChoice = "singleChoice",
  MultipleChoice = "multipleChoice"
};

export enum ItemType {
  category = "category",
  question = "question",
  template = "template",
  variant = "variant"
};

export enum GenerationStrategy {
  unique = "UNIQUE",
  compromise = "COMPROMISE"
}

export interface UserCredentialsPayload {
  username: string,
  password: string
};

export interface UserPayload {
  username: string
}

export interface SignInResponse {
  accessToken: string,
  tokenType: string
}

export interface Item {
  id: string,
  title: string,
  parentId: string
  type: ItemType
}

export interface Question extends Item {
  questionType: QuestionType,
  text: string,
  points: number,
  penalty: number,
  answers: Array<Answer>
}

export interface Answer {
  text: string,
  isCorrect: boolean
}

export interface Category extends Item {
}

export interface Template extends Item {
  topics: Array<Topic>
}

export interface Topic {
  questionCount: number,
  points: number,
  categories: Array<Array<{ title: string, id: string }>>
}

export interface Variant extends Item {
  date: Date;
  instructions: string,
  questions: Array<Question>
}
