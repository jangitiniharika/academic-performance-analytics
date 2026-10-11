/**
 * Form value helpers: convert the string a form control produces into the
 * number (or null) the REST API expects.
 */

export const toInt = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null;
  }
  return Number.parseInt(value, 10);
};

export const toNumber = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null;
  }
  return Number(value);
};
