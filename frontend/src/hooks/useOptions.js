import { useEffect, useState } from 'react';

/**
 * Loads a reference list once (for example every department) and maps it to
 * `{ value, label }` options for a dropdown.
 *
 * @param loader async (params) => PageResponse
 * @param mapper (item) => { value, label }
 */
export default function useOptions(loader, mapper) {
  const [options, setOptions] = useState([]);

  useEffect(() => {
    let active = true;
    loader({ size: 500, page: 0 })
      .then((page) => {
        if (active) {
          setOptions((page?.content ?? []).map(mapper));
        }
      })
      .catch(() => {
        if (active) {
          setOptions([]);
        }
      });
    return () => {
      active = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return options;
}
