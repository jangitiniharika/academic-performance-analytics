import { useCallback, useEffect, useState } from 'react';

/**
 * Tiny data-fetching hook that gives every screen the same three states:
 * "loading", "success" and "error". This keeps the UI honest - a component can
 * never show stale or fake data while a request is in flight.
 *
 * @param asyncFn function that returns a Promise (for example fetchHealth)
 * @param deps    dependency array, like useEffect
 */
export default function useAsync(asyncFn, deps = []) {
  const [state, setState] = useState({ status: 'loading', data: null, error: null });

  const run = useCallback(async () => {
    setState({ status: 'loading', data: null, error: null });
    try {
      const data = await asyncFn();
      setState({ status: 'success', data, error: null });
    } catch (error) {
      setState({ status: 'error', data: null, error });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    run();
  }, [run]);

  return { ...state, reload: run };
}
