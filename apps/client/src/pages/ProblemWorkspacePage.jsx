import { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProblemBySlug } from '../api/problemsApi';
import { createSubmission, getSubmission } from '../api/submissionsApi';
import { useAuth } from '../context/AuthContext';
import DifficultyBadge from '../components/DifficultyBadge';
import ExcalidrawEditor from '../components/ExcalidrawEditor';
import FeedbackPanel from '../components/FeedbackPanel';
import LoadingSpinner from '../components/LoadingSpinner';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import toast from 'react-hot-toast';
import { Pencil, FileText, BarChart3 } from 'lucide-react';

const TABS = [
  { id: 'design', label: 'Design', icon: Pencil },
  { id: 'writeup', label: 'Writeup', icon: FileText },
  { id: 'results', label: 'Results', icon: BarChart3 },
];

export default function ProblemWorkspacePage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const excalidrawRef = useRef(null);

  // Problem state
  const [problem, setProblem] = useState(null);
  const [loadingProblem, setLoadingProblem] = useState(true);
  const [problemError, setProblemError] = useState(null);

  // Workspace state
  const [activeTab, setActiveTab] = useState('design');
  const [writeup, setWriteup] = useState('');

  // Submission state
  const [submitting, setSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [pollingId, setPollingId] = useState(null);

  // Resize state
  const [leftWidth, setLeftWidth] = useState(40);
  const [isResizing, setIsResizing] = useState(false);

  // Fetch problem
  useEffect(() => {
    const fetchProblem = async () => {
      setLoadingProblem(true);
      setProblemError(null);
      try {
        const data = await getProblemBySlug(slug);
        setProblem(data);
      } catch (err) {
        setProblemError('Failed to load problem.');
        console.error(err);
      } finally {
        setLoadingProblem(false);
      }
    };
    fetchProblem();
  }, [slug]);

  // Poll for submission result
  useEffect(() => {
    if (!pollingId) return;

    const interval = setInterval(async () => {
      try {
        const result = await getSubmission(pollingId);
        setSubmissionResult(result);
        if (result.status === 'COMPLETED' || result.status === 'FAILED') {
          clearInterval(interval);
          setPollingId(null);
          setActiveTab('results');
          if (result.status === 'COMPLETED') {
            toast.success('Evaluation complete!');
          } else {
            toast.error('Evaluation failed.');
          }
        }
      } catch (err) {
        console.error('Polling error:', err);
      }
    }, 3000);

    return () => clearInterval(interval);
  }, [pollingId]);

  // Submit handler
  const handleSubmit = async () => {
    if (!isAuthenticated) {
      toast.error('Please sign in to submit.');
      navigate('/login');
      return;
    }

    if (!writeup.trim()) {
      toast.error('Please write a design explanation in the Writeup tab.');
      return;
    }

    const sceneData = excalidrawRef.current?.getSceneData();
    if (!sceneData || !sceneData.elements || sceneData.elements.length === 0) {
      toast.error('Please draw your design in the Design tab.');
      return;
    }

    setSubmitting(true);
    try {
      const result = await createSubmission({
        problemId: problem.id,
        excalidrawJson: sceneData,
        writeup,
      });
      setSubmissionResult(result);
      setActiveTab('results');

      if (result.status === 'PENDING') {
        setPollingId(result.id);
        toast('Submission received! Evaluating...', { icon: '⏳' });
      } else if (result.status === 'COMPLETED') {
        toast.success('Evaluation complete!');
      }
    } catch (err) {
      const message =
        err.response?.data?.message || 'Failed to submit. Please try again.';
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  // Resize handlers
  const handleMouseDown = useCallback((e) => {
    e.preventDefault();
    setIsResizing(true);
  }, []);

  useEffect(() => {
    if (!isResizing) return;

    const handleMouseMove = (e) => {
      const container = document.getElementById('workspace-container');
      if (!container) return;
      const rect = container.getBoundingClientRect();
      const newWidth = ((e.clientX - rect.left) / rect.width) * 100;
      setLeftWidth(Math.max(20, Math.min(70, newWidth)));
    };

    const handleMouseUp = () => {
      setIsResizing(false);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing]);

  // Loading / error states
  if (loadingProblem) {
    return (
      <div className="flex-1 flex items-center justify-center bg-lc-bg-primary">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (problemError || !problem) {
    return (
      <div className="flex-1 flex items-center justify-center bg-lc-bg-primary">
        <p className="text-sm text-lc-error">{problemError || 'Problem not found.'}</p>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col overflow-hidden">
      {/* Workspace */}
      <div
        id="workspace-container"
        className="flex-1 flex overflow-hidden"
        style={{ userSelect: isResizing ? 'none' : 'auto' }}
      >
        {/* ── Left Panel: Problem Description ── */}
        <div
          className="flex flex-col overflow-hidden bg-lc-bg-secondary"
          style={{ width: `${leftWidth}%` }}
        >
          {/* Panel header */}
          <div className="px-4 py-3 border-b border-lc-border flex-shrink-0">
            <div className="flex items-center gap-3">
              <h1 className="text-xl font-semibold text-lc-text-primary">
                {problem.title}
              </h1>
              <DifficultyBadge difficulty={problem.difficulty} />
            </div>
          </div>

          {/* Description content */}
          <div className="flex-1 overflow-y-auto px-4 py-4">
            <div className="problem-description">
              <ReactMarkdown remarkPlugins={[remarkGfm]}>
                {problem.description}
              </ReactMarkdown>
            </div>
          </div>
        </div>

        {/* ── Resize Handle ── */}
        <div
          className={`resize-handle ${isResizing ? 'active' : ''}`}
          onMouseDown={handleMouseDown}
        />

        {/* ── Right Panel: Tabs ── */}
        <div
          className="flex flex-col overflow-hidden bg-lc-bg-secondary"
          style={{ width: `${100 - leftWidth}%` }}
        >
          {/* Tab bar */}
          <div className="flex items-center border-b border-lc-border flex-shrink-0">
            {TABS.map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 ${
                    isActive
                      ? 'text-lc-text-primary border-lc-accent'
                      : 'text-lc-text-secondary border-transparent hover:text-lc-text-primary'
                  }`}
                >
                  <Icon size={14} />
                  {tab.label}
                </button>
              );
            })}
          </div>

          {/* Tab content */}
          <div className="flex-1 overflow-hidden">
            {/* Design tab */}
            <div
              className={`h-full ${activeTab === 'design' ? 'block' : 'hidden'}`}
            >
              <ExcalidrawEditor ref={excalidrawRef} />
            </div>

            {/* Writeup tab */}
            <div
              className={`h-full ${activeTab === 'writeup' ? 'flex flex-col' : 'hidden'}`}
            >
              <textarea
                value={writeup}
                onChange={(e) => setWriteup(e.target.value)}
                placeholder="Describe your design approach...&#10;&#10;Explain the components, data flow, trade-offs, and key decisions in your system design."
                className="flex-1 w-full bg-lc-bg-primary text-sm text-lc-text-primary placeholder-lc-text-tertiary p-4 resize-none focus:outline-none border-none font-sans leading-relaxed"
              />
              <div className="px-4 py-2 border-t border-lc-border flex-shrink-0">
                <p className="text-xs text-lc-text-tertiary">
                  {writeup.length} characters
                </p>
              </div>
            </div>

            {/* Results tab */}
            <div
              className={`h-full ${activeTab === 'results' ? 'block' : 'hidden'}`}
            >
              <FeedbackPanel
                status={submissionResult?.status || null}
                feedback={submissionResult?.feedback || ''}
                parsedDiagram={submissionResult?.parsedDiagram || ''}
              />
            </div>
          </div>
        </div>
      </div>

      {/* ── Bottom Bar ── */}
      <div className="h-[48px] bg-lc-bg-secondary border-t border-lc-border flex items-center justify-between px-4 flex-shrink-0">
        <button
          onClick={handleSubmit}
          disabled={submitting}
          className="bg-lc-accent hover:bg-lc-accent-hover text-black font-medium rounded-md px-5 py-1.5 text-sm transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          {submitting ? (
            <>
              <LoadingSpinner size="sm" />
              Submitting...
            </>
          ) : (
            'Submit'
          )}
        </button>

        <div className="flex items-center gap-2">
          {submissionResult && (
            <span
              className={`text-xs font-medium ${
                submissionResult.status === 'COMPLETED'
                  ? 'text-lc-success'
                  : submissionResult.status === 'FAILED'
                    ? 'text-lc-error'
                    : 'text-lc-accent'
              }`}
            >
              {submissionResult.status === 'COMPLETED' && '✓ Evaluated'}
              {submissionResult.status === 'FAILED' && '✗ Failed'}
              {submissionResult.status === 'PENDING' && '⏳ Evaluating...'}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
