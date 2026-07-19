import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getProblems } from '../api/problemsApi';
import DifficultyBadge from '../components/DifficultyBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import { Search, ChevronLeft, ChevronRight } from 'lucide-react';

const FILTERS = ['All', 'Easy', 'Medium', 'Hard'];

export default function ProblemsPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  const activeFilter = searchParams.get('difficulty') || 'All';
  const currentPage = parseInt(searchParams.get('page') || '0', 10);

  useEffect(() => {
    const fetchProblems = async () => {
      setLoading(true);
      setError(null);
      try {
        const params = {
          page: currentPage,
          size: 20,
        };
        if (activeFilter !== 'All') {
          params.difficulty = activeFilter;
        }
        const data = await getProblems(params);
        setProblems(data.content || []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      } catch (err) {
        setError('Failed to load problems. Is the backend running?');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProblems();
  }, [activeFilter, currentPage]);

  const handleFilterChange = (filter) => {
    const params = new URLSearchParams();
    if (filter !== 'All') params.set('difficulty', filter);
    params.set('page', '0');
    setSearchParams(params);
  };

  const handlePageChange = (page) => {
    const params = new URLSearchParams(searchParams);
    params.set('page', String(page));
    setSearchParams(params);
  };

  const filteredProblems = searchQuery
    ? problems.filter((p) =>
        p.title.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : problems;

  return (
    <div className="flex-1 overflow-y-auto">
      <div className="max-w-[1200px] mx-auto px-6 py-6">
        {/* Header */}
        <h1 className="text-xl font-semibold text-lc-text-primary mb-6">
          Problems
        </h1>

        {/* Filters + Search */}
        <div className="flex items-center justify-between mb-4">
          {/* Filter tabs */}
          <div className="flex items-center gap-1">
            {FILTERS.map((filter) => (
              <button
                key={filter}
                onClick={() => handleFilterChange(filter)}
                className={`px-3 py-1.5 text-sm font-medium rounded-md transition-colors ${
                  activeFilter === filter
                    ? 'text-lc-text-primary bg-lc-bg-hover'
                    : 'text-lc-text-secondary hover:text-lc-text-primary hover:bg-lc-bg-secondary'
                }`}
              >
                {filter}
              </button>
            ))}
          </div>

          {/* Search */}
          <div className="relative">
            <Search
              size={14}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-lc-text-tertiary"
            />
            <input
              type="text"
              placeholder="Search problems..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-lc-bg-secondary border border-lc-border rounded-md pl-8 pr-3 py-1.5 text-sm text-lc-text-primary placeholder-lc-text-tertiary focus:outline-none focus:border-lc-accent w-[240px]"
            />
          </div>
        </div>

        {/* Table */}
        {loading ? (
          <div className="flex items-center justify-center py-20">
            <LoadingSpinner size="lg" />
          </div>
        ) : error ? (
          <div className="flex items-center justify-center py-20 text-sm text-lc-error">
            {error}
          </div>
        ) : (
          <>
            <div className="border border-lc-border rounded-lg overflow-hidden">
              <table className="w-full">
                <thead>
                  <tr className="bg-lc-bg-secondary">
                    <th className="text-left text-xs font-medium text-lc-text-tertiary uppercase tracking-wider px-4 py-3 w-[60px]">
                      #
                    </th>
                    <th className="text-left text-xs font-medium text-lc-text-tertiary uppercase tracking-wider px-4 py-3">
                      Title
                    </th>
                    <th className="text-left text-xs font-medium text-lc-text-tertiary uppercase tracking-wider px-4 py-3 w-[100px]">
                      Difficulty
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProblems.length === 0 ? (
                    <tr>
                      <td
                        colSpan={3}
                        className="text-center py-12 text-sm text-lc-text-tertiary"
                      >
                        No problems found.
                      </td>
                    </tr>
                  ) : (
                    filteredProblems.map((problem, index) => (
                      <tr
                        key={problem.id}
                        onClick={() => navigate(`/problems/${problem.slug}`)}
                        className="border-t border-lc-border hover:bg-lc-bg-secondary cursor-pointer transition-colors"
                      >
                        <td className="px-4 py-3 text-sm text-lc-text-secondary">
                          {currentPage * 20 + index + 1}
                        </td>
                        <td className="px-4 py-3 text-sm text-lc-text-primary">
                          {problem.title}
                        </td>
                        <td className="px-4 py-3">
                          <DifficultyBadge difficulty={problem.difficulty} />
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-between mt-4">
                <p className="text-xs text-lc-text-tertiary">
                  {totalElements} problems
                </p>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="p-1.5 rounded-md text-lc-text-secondary hover:text-lc-text-primary hover:bg-lc-bg-hover disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronLeft size={16} />
                  </button>
                  {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
                    let page;
                    if (totalPages <= 7) {
                      page = i;
                    } else if (currentPage < 4) {
                      page = i;
                    } else if (currentPage > totalPages - 5) {
                      page = totalPages - 7 + i;
                    } else {
                      page = currentPage - 3 + i;
                    }
                    return (
                      <button
                        key={page}
                        onClick={() => handlePageChange(page)}
                        className={`min-w-[32px] h-[32px] rounded-md text-sm transition-colors ${
                          page === currentPage
                            ? 'bg-lc-accent text-black font-medium'
                            : 'text-lc-text-secondary hover:text-lc-text-primary hover:bg-lc-bg-hover'
                        }`}
                      >
                        {page + 1}
                      </button>
                    );
                  })}
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                    className="p-1.5 rounded-md text-lc-text-secondary hover:text-lc-text-primary hover:bg-lc-bg-hover disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
                  >
                    <ChevronRight size={16} />
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
