import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="page">
      <div className="card state state--empty" style={{ padding: '3rem' }}>
        <span className="state__icon" aria-hidden="true">
          404
        </span>
        <div>
          <div className="state__title">Page not found</div>
          <div className="state__message">
            The page you are looking for does not exist or has moved.
          </div>
        </div>
        <Link className="btn btn--primary" to="/">
          Back to overview
        </Link>
      </div>
    </div>
  );
}
