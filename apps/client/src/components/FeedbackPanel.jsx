import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import LoadingSpinner from './LoadingSpinner';
import { CheckCircle, XCircle, FileText } from 'lucide-react';

export default function FeedbackPanel({ status, feedback, parsedDiagram }) {
  // No submission yet
  if (!status) {
    return (
      <div className="flex flex-col items-center justify-center h-full text-lc-text-tertiary gap-3">
        <FileText size={40} strokeWidth={1} />
        <p className="text-sm">Submit your solution to see feedback here.</p>
      </div>
    );
  }

  // Pending evaluation
  if (status === 'PENDING') {
    return (
      <div className="flex flex-col items-center justify-center h-full text-lc-text-secondary gap-3">
        <LoadingSpinner size="lg" />
        <p className="text-sm">Evaluating your solution...</p>
        <p className="text-xs text-lc-text-tertiary">This may take a moment</p>
      </div>
    );
  }

  // Failed
  if (status === 'FAILED') {
    return (
      <div className="flex flex-col items-center justify-center h-full text-lc-error gap-3">
        <XCircle size={40} strokeWidth={1} />
        <p className="text-sm font-medium">Evaluation failed</p>
        <p className="text-xs text-lc-text-tertiary">
          Please try submitting again.
        </p>
      </div>
    );
  }

  // Completed — render feedback
  return (
    <div className="p-4 overflow-y-auto h-full">
      <div className="flex items-center gap-2 mb-3 pb-3 border-b border-lc-border">
        <CheckCircle size={16} className="text-lc-success" />
        <span className="text-sm font-medium text-lc-success">
          Evaluation Complete
        </span>
      </div>

      {parsedDiagram && (
        <div className="mb-4">
          <p className="text-xs font-medium text-lc-text-tertiary uppercase tracking-wider mb-2">
            Parsed Diagram
          </p>
          <pre className="text-xs text-lc-text-secondary bg-lc-bg-primary p-3 rounded-md border border-lc-border overflow-x-auto whitespace-pre-wrap">
            {parsedDiagram}
          </pre>
        </div>
      )}

      <div className="markdown-content">
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{feedback}</ReactMarkdown>
      </div>
    </div>
  );
}
