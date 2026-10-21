import { useMemo, useState } from 'react';

const categories = ['All stories', 'Slow living', 'Creativity', 'Travel', 'Wellbeing'];

const posts = [
  {
    id: 1,
    category: 'Slow living',
    title: 'The gentle art of doing one thing at a time',
    excerpt: 'A small invitation to slow the rush, notice the moment, and let one thing be enough.',
    author: 'Maya Bennett',
    date: 'October 18, 2026',
    readTime: '6 min read',
    image:
      'https://images.unsplash.com/photo-1445116572660-236099ec97a0?auto=format&fit=crop&w=1000&q=85',
    imageAlt: 'A sunlit coffee shop with a quiet table by the window',
    body: [
      'Somewhere along the way, we started treating a full calendar like a small personal victory. We answer messages while making coffee, make plans while walking to them, and wonder why the days feel like they have gone by without us.',
      'Lately, I have been trying something much simpler: doing one thing at a time. When the kettle is on, I listen to it. When I meet a friend, I leave my phone in my bag. It is not a grand new routine, just a little room for the moment I am already in.',
      'The lovely surprise is that ordinary things begin to feel less ordinary. A warm mug, a conversation that wanders, the light moving across the kitchen floor: they were all here before. I just had to be here too.',
    ],
  },
  {
    id: 2,
    category: 'Creativity',
    title: 'Make something badly. Make it anyway.',
    excerpt: 'On letting go of perfect first drafts and finding your way back to play.',
    author: 'Theo James',
    date: 'October 12, 2026',
    readTime: '4 min read',
    image:
      'https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=1000&q=85',
    imageAlt: 'A notebook and pen ready for a new idea',
    body: [
      'The blank page has a way of making even playful ideas feel like important decisions. We wait for a better pen, a quieter morning, a little more certainty.',
      'But your first attempt is allowed to be awkward. A lopsided sketch is still a sketch. A paragraph you rewrite later is still a beginning. Making something gives you a place to start, and a place to start is much more useful than a perfect idea you never try.',
      'So this is your permission slip to make the messy version. Make it for the fun of it. You can figure out what it wants to be as you go.',
    ],
  },
  {
    id: 3,
    category: 'Travel',
    title: 'A weekend with nowhere much to be',
    excerpt: 'Little streets, long lunches, and the joy of leaving a few hours unplanned.',
    author: 'Maya Bennett',
    date: 'October 05, 2026',
    readTime: '7 min read',
    image:
      'https://images.unsplash.com/photo-1470770841072-f978cf4d019e?auto=format&fit=crop&w=1000&q=85',
    imageAlt: 'A peaceful mountain lake surrounded by green hills',
    body: [
      'We took the train without a checklist. There was a place to stay and a rough idea of where the water was, but everything else could wait until we got there.',
      'Without the usual rush from one highlight to another, we found a bakery we would have missed, took the longer path back, and sat by the lake until the conversation ran out and then started again.',
      'I came home with no dramatic stories and a camera roll full of quiet corners. It turns out a trip does not need to be packed to stay with you.',
    ],
  },
  {
    id: 4,
    category: 'Wellbeing',
    title: 'A softer start to the morning',
    excerpt: 'A few small rituals that make the first hour feel like yours again.',
    author: 'Leah Chen',
    date: 'September 28, 2026',
    readTime: '5 min read',
    image:
      'https://images.unsplash.com/photo-1499209974431-9dddcece7f88?auto=format&fit=crop&w=1000&q=85',
    imageAlt: 'A peaceful morning with a cup of tea and a book',
    body: [
      'A good morning routine does not have to look like a routine at all. It can be opening the curtains before opening your inbox, or drinking a glass of water while the house is still quiet.',
      'The trick, for me, is keeping it small enough to actually happen. No perfect sunrise, no complicated plan. Just a little space between waking up and letting the day rush in.',
      'Some mornings it is tea. Some mornings it is five extra minutes under the covers. Both count. The goal is not a better version of you; it is a kinder hello to the version who is already here.',
    ],
  },
];

function ArrowIcon() {
  return <span aria-hidden="true">↗</span>;
}

function App() {
  const [activeCategory, setActiveCategory] = useState('All stories');
  const [search, setSearch] = useState('');
  const [selectedPost, setSelectedPost] = useState(null);
  const [email, setEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  const filteredPosts = useMemo(() => {
    const query = search.trim().toLowerCase();
    return posts.filter((post) => {
      const inCategory = activeCategory === 'All stories' || post.category === activeCategory;
      const matchesSearch =
        !query ||
        `${post.title} ${post.excerpt} ${post.category} ${post.author}`
          .toLowerCase()
          .includes(query);
      return inCategory && matchesSearch;
    });
  }, [activeCategory, search]);

  function handleSubscribe(event) {
    event.preventDefault();
    if (email.trim()) setSubscribed(true);
  }

  function closePost() {
    setSelectedPost(null);
  }

  return (
    <div className="site-shell">
      <header className="site-header">
        <a className="brand" href="#home" aria-label="Little Notes home">
          <span className="brand-mark">l<span>.</span></span>
          <span className="brand-name">little notes</span>
        </a>
        <button
          className="menu-toggle"
          type="button"
          aria-label={menuOpen ? 'Close navigation' : 'Open navigation'}
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen(!menuOpen)}
        >
          {menuOpen ? '×' : '☰'}
        </button>
        <nav className={menuOpen ? 'main-nav is-open' : 'main-nav'} aria-label="Main navigation">
          <a href="#stories" onClick={() => setMenuOpen(false)}>Stories</a>
          <a href="#about" onClick={() => setMenuOpen(false)}>About</a>
          <a className="nav-join" href="#newsletter" onClick={() => setMenuOpen(false)}>
            Get the Sunday note <ArrowIcon />
          </a>
        </nav>
      </header>

      <main id="home">
        <section className="hero">
          <div className="hero-copy">
            <span className="eyebrow"><span className="eyebrow-dot" /> A little corner of the internet</span>
            <h1>Stories for a<br /><em>slower day.</em></h1>
            <p>Thoughts on making things, finding your way, and enjoying the little bits in between.</p>
            <a className="button button-dark" href="#stories">Find a story <span aria-hidden="true">↓</span></a>
            <div className="reader-note">
              <div className="reader-avatars" aria-hidden="true">
                <span>M</span><span>J</span><span>A</span>
              </div>
              <span>A quiet read for <strong>your kind of day</strong></span>
            </div>
          </div>
          <div className="hero-art">
            <img
              src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1200&q=90"
              alt="A warm, welcoming reading nook filled with soft afternoon light"
            />
            <div className="image-caption"><span>take your time</span><span>✳</span></div>
            <div className="hero-sticker" aria-hidden="true">a little<br />more slow<br /><span>♡</span></div>
          </div>
        </section>

        <section className="stories-section" id="stories">
          <div className="section-heading">
            <div>
              <span className="eyebrow">A few things worth a pause</span>
              <h2>Fresh from the notebook<span>.</span></h2>
            </div>
            <label className="search-box">
              <span aria-hidden="true">⌕</span>
              <input
                type="search"
                placeholder="Find a story..."
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                aria-label="Search stories"
              />
            </label>
          </div>

          <div className="category-list" aria-label="Filter stories by category">
            {categories.map((category) => (
              <button
                className={activeCategory === category ? 'category-chip active' : 'category-chip'}
                key={category}
                type="button"
                aria-pressed={activeCategory === category}
                onClick={() => setActiveCategory(category)}
              >
                {category}
              </button>
            ))}
          </div>

          {filteredPosts.length ? (
            <div className="post-grid">
              {filteredPosts.map((post) => (
                <article className="post-card" key={post.id}>
                  <button
                    className="post-image-button"
                    type="button"
                    onClick={() => setSelectedPost(post)}
                    aria-label={`Read ${post.title}`}
                  >
                    <img src={post.image} alt={post.imageAlt} loading="lazy" />
                    <span className="image-arrow"><ArrowIcon /></span>
                  </button>
                  <div className="post-meta"><span>{post.category}</span><span>{post.readTime}</span></div>
                  <button className="post-title-button" type="button" onClick={() => setSelectedPost(post)}>
                    <h3>{post.title}</h3>
                  </button>
                  <p className="post-excerpt">{post.excerpt}</p>
                  <div className="post-byline"><span>{post.author}</span><span>{post.date}</span></div>
                </article>
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <span aria-hidden="true">☼</span>
              <h3>No stories found just yet.</h3>
              <p>Try another search or choose a different topic.</p>
              <button
                className="text-button"
                type="button"
                onClick={() => { setSearch(''); setActiveCategory('All stories'); }}
              >
                Show all stories <ArrowIcon />
              </button>
            </div>
          )}
        </section>

        <section className="about-section" id="about">
          <div className="about-art"><span className="about-flower" aria-hidden="true">✿</span></div>
          <div className="about-copy">
            <span className="eyebrow">A note from the editor</span>
            <h2>For the curious, the daydreamers, and the <em>in-between.</em></h2>
            <p>Little Notes is an independent blog about paying attention. We share honest stories, thoughtful ideas, and gentle reminders to make a little room for what matters.</p>
            <a className="text-button" href="#newsletter">A little more about us <ArrowIcon /></a>
          </div>
        </section>

        <section className="newsletter" id="newsletter">
          <div className="newsletter-spark" aria-hidden="true">✳</div>
          <span className="eyebrow">A good thing for your inbox</span>
          <h2>A Sunday note,<br /><em>now and then.</em></h2>
          <p>One thoughtful little email with new stories and things worth keeping. No noise, ever.</p>
          {subscribed ? (
            <div className="success-message" role="status">You’re on the list. Look out for a little note soon ♡</div>
          ) : (
            <form className="newsletter-form" onSubmit={handleSubscribe}>
              <label className="sr-only" htmlFor="email">Your email address</label>
              <input
                id="email"
                type="email"
                placeholder="Your email address"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
              <button className="button button-dark" type="submit">Count me in <ArrowIcon /></button>
            </form>
          )}
          <span className="newsletter-footnote">Just the good stuff. Unsubscribe whenever.</span>
        </section>
      </main>

      <footer className="site-footer">
        <a className="brand" href="#home">
          <span className="brand-mark">l<span>.</span></span>
          <span className="brand-name">little notes</span>
        </a>
        <span>Made slowly, with care. © 2026 Little Notes</span>
        <a href="mailto:hello@littlenotes.example">Say hello <ArrowIcon /></a>
      </footer>

      {selectedPost && (
        <div className="modal-backdrop" role="presentation" onMouseDown={closePost}>
          <article
            className="article-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="article-title"
            onMouseDown={(event) => event.stopPropagation()}
          >
            <button className="modal-close" type="button" onClick={closePost} aria-label="Close story">×</button>
            <img className="modal-image" src={selectedPost.image} alt={selectedPost.imageAlt} />
            <div className="modal-content">
              <div className="post-meta"><span>{selectedPost.category}</span><span>{selectedPost.readTime}</span></div>
              <h2 id="article-title">{selectedPost.title}</h2>
              <div className="post-byline"><span>By {selectedPost.author}</span><span>{selectedPost.date}</span></div>
              {selectedPost.body.map((paragraph) => <p key={paragraph}>{paragraph}</p>)}
              <button className="text-button" type="button" onClick={closePost}>Back to all stories <span aria-hidden="true">↗</span></button>
            </div>
          </article>
        </div>
      )}
    </div>
  );
}

export default App;
