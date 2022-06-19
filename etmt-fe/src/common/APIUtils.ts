import { Variant } from '@testing-library/react';
import { API_BASE_URL } from '../Constants';
import { GenerationStrategy, Item, ItemType, Nullable, SignInResponse, UserCredentialsPayload } from './Common';

export const API_REQUEST_TEST_TIMEOUT_MS = 0;

const DEBUG = false;

enum RequestMethod {
  POST = 'POST',
  GET = 'GET',
  DELETE = 'DELETE'
}

interface RequestOptions {
  url: string,
  method: RequestMethod,
  body?: string
}

export interface ApiRequestError {
  message: string,
  status: number
}

const apiRequest = <T>(reqOptions: RequestOptions, token: Nullable<string> = null): Promise<T> => {
  const headers = new Headers({
    'Content-Type': 'application/json',
  })

  if (token) {
    headers.append('Authorization', 'Bearer ' + token)
  }

  const defaults = { headers: headers };
  reqOptions = Object.assign({}, defaults, reqOptions);

  DEBUG && console.log("api request json: ", reqOptions);

  return new Promise((resolve) => {
    setTimeout(() => {
      resolve("timeout finished");
    }, API_REQUEST_TEST_TIMEOUT_MS);
  }).then(() => {
    return fetch(reqOptions.url, reqOptions)
      .then(response => {
        DEBUG && console.log("api response json: ", response);

        if (!response.ok) {
          return Promise.reject({ message: "Error fetching api request", status: response.status });
        }

        return response.json().catch(
          err => {
            console.log("error parsing json response", err);
            return Promise.reject({ message: "Error parsing server response" });
          }
        ) as Promise<T>;
      });
  })
}

const apiRequestBlob = (reqOptions: RequestOptions, token: Nullable<string> = null): Promise<Blob> => {
  const headers = new Headers({
    'Content-Type': 'application/json',
  })

  if (token) {
    headers.append('Authorization', 'Bearer ' + token)
  }

  const defaults = { headers: headers };
  reqOptions = Object.assign({}, defaults, reqOptions);

  DEBUG && console.log("api request (blob): ", reqOptions);

  return new Promise((resolve) => {
    setTimeout(() => {
      resolve("timeout finished");
    }, API_REQUEST_TEST_TIMEOUT_MS);
  }).then(() => {
    return fetch(reqOptions.url, reqOptions)
      .then(response => {
        if (!response.ok) {
          return Promise.reject({ message: "Error fetching api request", status: response.status });
        }

        return response.blob().catch(
          err => {
            console.log("error parsing blob response", err);
            return Promise.reject({ message: "Error parsing server response" });
          }
        ) as Promise<Blob>;
      })
  })
}

export const apiSignIn = (requestData: UserCredentialsPayload) => {
  return apiRequest<SignInResponse>({
    url: API_BASE_URL + "/auth/login",
    method: RequestMethod.POST,
    body: JSON.stringify(requestData)
  });
}

export const apiSignUp = (requestData: UserCredentialsPayload) => {
  return apiRequest<{ message: string }>({
    url: API_BASE_URL + "/auth/signup",
    method: RequestMethod.POST,
    body: JSON.stringify(requestData)
  });
}

export const apiCheckUsernameAvailability = (username: string) => {
  return apiRequest<{ true: boolean }>({
    url: API_BASE_URL + "/auth/checkUsernameAvailability?username=" + username,
    method: RequestMethod.GET
  });
}

export const apiGetLibrary = (token: Nullable<string>) => {
  return apiRequest<Array<Item>>({
    url: API_BASE_URL + "/library",
    method: RequestMethod.GET,
  }, token);
}

export const apiPostItem = <T extends Item>(item: T, token: Nullable<string>) => {
  return apiRequest<{}>({
    url: API_BASE_URL + getPathForType(item.type) + (item.id ? `/${item.id}` : ""),
    method: RequestMethod.POST,
    body: JSON.stringify(item)
  }, token)
}

export const apiMoveItem = (item: Item, newParentId: string, position: number, token: Nullable<string>) => {
  return apiRequest<{}>({
    url: API_BASE_URL + `/library/move?itemId=${item.id}&newParentId=${newParentId}&position=${position}`,
    method: RequestMethod.POST,
  }, token)
}

export const apiGetItem = <T extends Item>(id: string, type: ItemType, token: Nullable<string>): Promise<T> => {
  return apiRequest<T>({
    url: API_BASE_URL + getPathForType(type) + "/" + id,
    method: RequestMethod.GET,
  }, token);
}

export const apiDeleteItem = (id: string, deleteChildren: boolean = false, token: Nullable<string>) => {
  return apiRequest<{}>({
    url: API_BASE_URL + "/library/" + id + "?deleteChildren=" + deleteChildren,
    method: RequestMethod.DELETE,
  }, token);
}

export const apiCheckQuestionCount = (parentId: string, points: number, count: number, token: Nullable<string>) => {
  return apiRequest<{ categoryTitle: string, sufficient: boolean }>({
    url: API_BASE_URL + "/variants/check?categoryId=" + parentId + "&points=" + points + "&count=" + count,
    method: RequestMethod.GET
  }, token);
}

export const apiGenerateVariants = (templateId: string, strategy: GenerationStrategy, token: Nullable<string>) => {
  return apiRequest<Array<Variant>>({
    url: API_BASE_URL + "/variants/generate?testTemplateId=" + templateId + "&strategy=" + strategy,
    method: RequestMethod.GET,
  }, token)
}

export const apiExportXml = (id: string, parent: string, token: Nullable<string>) => {
  return apiRequestBlob({
    url: API_BASE_URL + "/variants/xml?testVariantId=" + id + "&parentCategoryName=" + parent,
    method: RequestMethod.GET
  }, token)
}

export const apiExportPdf = (id: string, token: Nullable<string>) => {
  return apiRequestBlob({
    url: API_BASE_URL + "/variants/pdf?testVariantId=" + id,
    method: RequestMethod.GET
  }, token);
}

const getPathForType = (type: ItemType) => {
  switch (type) {
    case ItemType.category:
      return "/categories"
    case ItemType.question:
      return "/questions"
    case ItemType.template:
      return "/templates"
    case ItemType.variant:
      return "/variants"
    default:
      throw new Error(`item type ${type} is not supported`);
  }
}
