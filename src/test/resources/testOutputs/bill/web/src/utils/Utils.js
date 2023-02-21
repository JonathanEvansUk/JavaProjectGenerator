export const replaceNullsWithUndefined = (object) => {
  return Object.fromEntries(
        Object.entries(object).map(([key, value]) =>
        [key, value === null ? undefined : value] ));
};