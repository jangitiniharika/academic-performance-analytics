import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * Fetches a paginated list and re-fetches whenever the query parameters change.
 *
 * @param loader  async (params) => PageResponse  (kept in a ref so callers do
 *                not need to memoise it)
 * @param initialParams initial query string params (page/size/filters)
 */
export default function usePaginatedList(loader, initialParams = {}) {
  const loaderRef = useRef(loader);
  loaderRef.current = loader;

  const [params, setParams] = useState({ page: 0, size: 10, ...initialParams });
  const [data, setData] = useState({
    content: [],
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 1,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const reload = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await loaderRef.current(params);
      setData(response);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [params]);

  useEffect(() => {
    reload();
  }, [reload]);

  const setPage = useCallback((page) => {
    setParams((previous) => ({ ...previous, page }));
  }, []);

  const setFilter = useCallback((patch) => {
    setParams((previous) => ({ ...previous, ...patch, page: 0 }));
  }, []);

  return {
    ...data,
    rows: data.content ?? [],
    loading,
    error,
    params,
    setFilter,
    setPage,
    reload,
  };
}
