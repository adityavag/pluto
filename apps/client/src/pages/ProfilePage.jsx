import { useEffect, useState, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getUserProfile } from '../api/usersApi';
import LoadingSpinner from '../components/LoadingSpinner';
import { Calendar, Award, CheckCircle, Flame, Activity } from 'lucide-react';
import toast from 'react-hot-toast';

export default function ProfilePage() {
  const { username } = useParams();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchProfile() {
      setLoading(true);
      try {
        const data = await getUserProfile(username);
        setProfile(data);
      } catch (err) {
        console.error('Error fetching profile:', err);
        toast.error('Failed to load profile. User may not exist.');
      } finally {
        setLoading(false);
      }
    }
    fetchProfile();
  }, [username]);

  // Compute contribution calendar grid of 53 weeks x 7 days
  const calendarGrid = useMemo(() => {
    if (!profile) return [];

    const today = new Date();
    const calendarData = [];

    // Calculate dates for the past 365 days
    for (let i = 365; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(today.getDate() - i);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;
      const count = profile.submissionCalendar?.[dateStr] || 0;
      calendarData.push({
        date: dateStr,
        count,
        dayOfWeek: d.getDay(), // 0 = Sunday, 1 = Monday, ...
        month: d.getMonth(),   // 0-11
      });
    }

    // Pad the start of grid to align weeks properly starting on Sunday
    const firstDayOfWeek = calendarData[0].dayOfWeek;
    const paddedDays = [];
    for (let i = 0; i < firstDayOfWeek; i++) {
      paddedDays.push(null);
    }
    const allDays = [...paddedDays, ...calendarData];

    // Chunk into weeks (7 days each)
    const weeks = [];
    for (let i = 0; i < allDays.length; i += 7) {
      weeks.push(allDays.slice(i, i + 7));
    }
    return weeks;
  }, [profile]);

  if (loading) {
    return (
      <div className="flex-grow flex items-center justify-center bg-lc-bg-primary">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="flex-grow flex flex-col items-center justify-center bg-lc-bg-primary text-lc-text-secondary">
        <p className="text-base font-medium">User profile not found.</p>
        <Link to="/" className="mt-4 text-sm text-lc-accent hover:underline">
          Go back to Problems
        </Link>
      </div>
    );
  }

  const { solvedStats, streakStats, recentSubmissions, user } = profile;

  // Circular progress stats
  const solved = solvedStats?.solvedCount || 0;
  const total = solvedStats?.totalCount || 0;
  const percent = total > 0 ? (solved / total) * 100 : 0;
  const radius = 52;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (percent / 100) * circumference;

  // Format joined date
  const joinedDate = user?.createdAt
    ? new Date(user.createdAt).toLocaleDateString('en-US', {
        month: 'long',
        year: 'numeric',
      })
    : 'Unknown';

  const getHeatmapColor = (count) => {
    if (count === 0) return 'bg-zinc-800'; // No submissions
    if (count === 1) return 'bg-green-950/80 border border-green-900/30';
    if (count === 2) return 'bg-green-900 border border-green-800/40';
    if (count === 3) return 'bg-green-700';
    return 'bg-green-500';
  };

  const formatTooltipDate = (dateStr) => {
    if (!dateStr) return '';
    const [year, month, day] = dateStr.split('-');
    const date = new Date(year, month - 1, day);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  const getDifficultyColor = (diff) => {
    const d = diff?.toUpperCase();
    if (d === 'EASY') return 'text-lc-success';
    if (d === 'MEDIUM') return 'text-lc-accent';
    if (d === 'HARD') return 'text-lc-error';
    return 'text-lc-text-secondary';
  };

  const getDifficultyBg = (diff) => {
    const d = diff?.toUpperCase();
    if (d === 'EASY') return 'bg-emerald-500/10 text-emerald-400';
    if (d === 'MEDIUM') return 'bg-amber-500/10 text-amber-400';
    if (d === 'HARD') return 'bg-rose-500/10 text-rose-400';
    return 'bg-zinc-500/10 text-zinc-400';
  };

  const formatRelativeTime = (isoStr) => {
    const diffMs = new Date() - new Date(isoStr);
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHrs = Math.floor(diffMin / 60);
    const diffDays = Math.floor(diffHrs / 24);

    if (diffDays > 0) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    if (diffHrs > 0) return `${diffHrs} hour${diffHrs > 1 ? 's' : ''} ago`;
    if (diffMin > 0) return `${diffMin} minute${diffMin > 1 ? 's' : ''} ago`;
    return 'just now';
  };

  return (
    <div className="flex-grow bg-lc-bg-primary py-8 px-6 overflow-y-auto">
      <div className="max-w-[1100px] mx-auto grid grid-cols-1 md:grid-cols-4 gap-6">
        {/* ── Left Side Column: User Info & Streaks ── */}
        <div className="md:col-span-1 flex flex-col gap-5">
          {/* Main User Card */}
          <div className="bg-lc-bg-secondary border border-lc-border rounded-xl p-5 flex flex-col items-center text-center">
            {/* Avatar Placeholder */}
            <div className="w-20 h-20 bg-zinc-800 border border-lc-border rounded-full flex items-center justify-center text-lc-text-secondary font-bold text-3xl mb-4">
              {user?.username?.substring(0, 2).toUpperCase()}
            </div>
            <h2 className="text-xl font-semibold text-lc-text-primary mb-1">
              {user?.username}
            </h2>
            <div className="flex items-center gap-1.5 text-xs text-lc-text-tertiary mt-2">
              <Calendar size={13} />
              <span>Joined {joinedDate}</span>
            </div>
          </div>

          {/* Streaks Widget */}
          <div className="bg-lc-bg-secondary border border-lc-border rounded-xl p-5">
            <h3 className="text-sm font-semibold text-lc-text-primary mb-4 flex items-center gap-2">
              <Flame size={16} className="text-orange-500 fill-orange-500/10" />
              Active Streaks
            </h3>
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-lc-bg-primary/50 border border-lc-border/60 rounded-lg p-3 text-center">
                <span className="block text-xl font-bold text-orange-500">
                  {streakStats?.currentStreak || 0}
                </span>
                <span className="text-[10px] text-lc-text-tertiary font-medium uppercase tracking-wider">
                  Current Streak
                </span>
              </div>
              <div className="bg-lc-bg-primary/50 border border-lc-border/60 rounded-lg p-3 text-center">
                <span className="block text-xl font-bold text-lc-accent">
                  {streakStats?.maxStreak || 0}
                </span>
                <span className="text-[10px] text-lc-text-tertiary font-medium uppercase tracking-wider">
                  Max Streak
                </span>
              </div>
            </div>
            <div className="mt-4 pt-3 border-t border-lc-border/40 flex items-center justify-between text-xs text-lc-text-secondary">
              <span>Total Active Days</span>
              <span className="font-bold text-lc-text-primary">
                {streakStats?.totalActiveDays || 0}
              </span>
            </div>
          </div>
        </div>

        {/* ── Right Side Column: Solved Stats, Heatmap, Activity ── */}
        <div className="md:col-span-3 flex flex-col gap-6">
          {/* Solved Problems Dashboard */}
          <div className="bg-lc-bg-secondary border border-lc-border rounded-xl p-6">
            <h3 className="text-sm font-semibold text-lc-text-primary mb-6 flex items-center gap-2">
              <Award size={16} className="text-lc-accent" />
              Solved Statistics
            </h3>

            <div className="flex flex-col sm:flex-row items-center gap-8">
              {/* Progress Circle Wrapper */}
              <div className="relative flex items-center justify-center flex-shrink-0">
                <svg className="w-[128px] h-[128px] transform -rotate-90">
                  <circle
                    cx="64"
                    cy="64"
                    r={radius}
                    className="stroke-zinc-800"
                    strokeWidth="8"
                    fill="transparent"
                  />
                  <circle
                    cx="64"
                    cy="64"
                    r={radius}
                    className="stroke-lc-accent"
                    strokeWidth="8"
                    fill="transparent"
                    strokeDasharray={circumference}
                    strokeDashoffset={strokeDashoffset}
                    strokeLinecap="round"
                  />
                </svg>
                <div className="absolute inset-0 flex flex-col items-center justify-center">
                  <span className="text-2xl font-bold text-lc-text-primary">
                    {solved}
                  </span>
                  <span className="text-xs text-lc-text-tertiary">/{total}</span>
                  <span className="text-[10px] text-lc-success font-medium mt-0.5 uppercase tracking-wide">
                    Solved
                  </span>
                </div>
              </div>

              {/* Progress Bars Breakdown */}
              <div className="flex-1 w-full flex flex-col gap-4">
                {/* Easy */}
                <div>
                  <div className="flex justify-between text-xs font-medium mb-1">
                    <span className="text-emerald-400">Easy</span>
                    <span className="text-lc-text-secondary">
                      {solvedStats?.easySolved || 0}
                      <span className="text-lc-text-tertiary">
                        /{solvedStats?.easyTotal || 0}
                      </span>
                    </span>
                  </div>
                  <div className="h-1.5 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-emerald-500 rounded-full"
                      style={{
                        width: `${
                          solvedStats?.easyTotal > 0
                            ? (solvedStats.easySolved / solvedStats.easyTotal) * 100
                            : 0
                        }%`,
                      }}
                    />
                  </div>
                </div>

                {/* Medium */}
                <div>
                  <div className="flex justify-between text-xs font-medium mb-1">
                    <span className="text-amber-400">Medium</span>
                    <span className="text-lc-text-secondary">
                      {solvedStats?.mediumSolved || 0}
                      <span className="text-lc-text-tertiary">
                        /{solvedStats?.mediumTotal || 0}
                      </span>
                    </span>
                  </div>
                  <div className="h-1.5 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-amber-500 rounded-full"
                      style={{
                        width: `${
                          solvedStats?.mediumTotal > 0
                            ? (solvedStats.mediumSolved / solvedStats.mediumTotal) * 100
                            : 0
                        }%`,
                      }}
                    />
                  </div>
                </div>

                {/* Hard */}
                <div>
                  <div className="flex justify-between text-xs font-medium mb-1">
                    <span className="text-rose-400">Hard</span>
                    <span className="text-lc-text-secondary">
                      {solvedStats?.hardSolved || 0}
                      <span className="text-lc-text-tertiary">
                        /{solvedStats?.hardTotal || 0}
                      </span>
                    </span>
                  </div>
                  <div className="h-1.5 w-full bg-zinc-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-rose-500 rounded-full"
                      style={{
                        width: `${
                          solvedStats?.hardTotal > 0
                            ? (solvedStats.hardSolved / solvedStats.hardTotal) * 100
                            : 0
                        }%`,
                      }}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Attempting count footer */}
            {solvedStats?.attemptingCount > 0 && (
              <div className="mt-5 pt-3 border-t border-lc-border/30 text-xs text-lc-text-secondary">
                Currently attempting <span className="font-semibold text-lc-text-primary">{solvedStats.attemptingCount}</span> problem{solvedStats.attemptingCount > 1 ? 's' : ''}.
              </div>
            )}
          </div>

          {/* Submission Heatmap Grid */}
          <div className="bg-lc-bg-secondary border border-lc-border rounded-xl p-6">
            <h3 className="text-sm font-semibold text-lc-text-primary mb-4 flex items-center gap-2">
              <Activity size={16} className="text-emerald-500" />
              Submissions in the past year
            </h3>

            {/* Grid Container */}
            <div className="flex gap-[3px] overflow-x-auto pb-3 select-none">
              {calendarGrid.map((week, wIdx) => (
                <div key={wIdx} className="flex flex-col gap-[3px] flex-shrink-0">
                  {week.map((day, dIdx) => {
                    if (!day) {
                      return (
                        <div
                          key={dIdx}
                          className="w-[10px] h-[10px] bg-transparent"
                        />
                      );
                    }
                    const colorClass = getHeatmapColor(day.count);
                    return (
                      <div
                        key={dIdx}
                        className={`w-[10px] h-[10px] rounded-[1px] ${colorClass} transition-colors cursor-pointer relative group`}
                      >
                        {/* Tooltip on hover */}
                        <span className="absolute bottom-full left-1/2 -translate-x-1/2 mb-1.5 hidden group-hover:block z-50 bg-zinc-950 text-[10px] text-lc-text-primary px-2.5 py-1 rounded-md shadow-lg border border-zinc-800 pointer-events-none whitespace-nowrap">
                          {day.count === 0 ? 'No' : day.count} submission{day.count !== 1 ? 's' : ''} on {formatTooltipDate(day.date)}
                        </span>
                      </div>
                    );
                  })}
                </div>
              ))}
            </div>

            {/* Heatmap Legend */}
            <div className="flex items-center justify-end gap-1.5 mt-2 text-[10px] text-lc-text-tertiary">
              <span>Less</span>
              <div className="w-[10px] h-[10px] rounded-[1px] bg-zinc-800" />
              <div className="w-[10px] h-[10px] rounded-[1px] bg-green-950/80 border border-green-900/30" />
              <div className="w-[10px] h-[10px] rounded-[1px] bg-green-900 border border-green-800/40" />
              <div className="w-[10px] h-[10px] rounded-[1px] bg-green-700" />
              <div className="w-[10px] h-[10px] rounded-[1px] bg-green-500" />
              <span>More</span>
            </div>
          </div>

          {/* Recent Activity list */}
          <div className="bg-lc-bg-secondary border border-lc-border rounded-xl overflow-hidden">
            {/* Header tabs style */}
            <div className="px-6 py-4 border-b border-lc-border flex items-center justify-between">
              <h3 className="text-sm font-semibold text-lc-text-primary flex items-center gap-2">
                <CheckCircle size={16} className="text-lc-success" />
                Recent AC Submissions
              </h3>
            </div>

            {/* List */}
            {recentSubmissions && recentSubmissions.length > 0 ? (
              <div className="divide-y divide-lc-border/50">
                {recentSubmissions.map((sub) => (
                  <div
                    key={sub.id}
                    className="px-6 py-4 flex items-center justify-between hover:bg-zinc-800/20 transition-colors"
                  >
                    <div className="flex items-center gap-3">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${getDifficultyBg(
                          sub.difficulty
                        )}`}
                      >
                        {sub.difficulty}
                      </span>
                      <Link
                        to={`/problems/${sub.problemSlug}`}
                        className="text-sm font-medium text-lc-text-primary hover:text-lc-accent transition-colors"
                      >
                        {sub.problemTitle}
                      </Link>
                    </div>
                    <span className="text-xs text-lc-text-tertiary">
                      {formatRelativeTime(sub.createdAt)}
                    </span>
                  </div>
                ))}
              </div>
            ) : (
              <div className="px-6 py-8 text-center text-sm text-lc-text-tertiary">
                No accepted submissions found. Start designing problems to see your history!
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
