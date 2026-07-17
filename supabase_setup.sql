-- PrintXpress Supabase setup
-- Run this in the Supabase dashboard: SQL Editor -> New query -> paste -> Run.
-- Safe to re-run: every table uses "if not exists", every column addition uses
-- "add column if not exists", and every policy is dropped-then-recreated.
--
-- Column names are intentionally quoted camelCase (e.g. "userId", "basePrice") to match
-- the Android app's Java model field names exactly, so Gson can map JSON <-> POJO with
-- zero custom serialization code.

-- ============ profiles ============
-- Mirrors the "User" model. id = the Supabase Auth user's id (auth.users.id).
create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    username text unique,
    profile text,
    name text,
    phone text,
    email text,
    address text
);

-- "role" was added after the table already existed for some projects - "create table
-- if not exists" alone would silently skip adding it, so add it explicitly here too.
alter table public.profiles add column if not exists role text default 'customer';

alter table public.profiles enable row level security;

-- Anyone (including anonymous/pre-login requests) can read profiles - needed so
-- username-based login can look up the matching email before the user is authenticated.
drop policy if exists "profiles are publicly readable" on public.profiles;
create policy "profiles are publicly readable"
    on public.profiles for select
    using (true);

-- A user can only create/update their own profile row.
drop policy if exists "users can insert their own profile" on public.profiles;
create policy "users can insert their own profile"
    on public.profiles for insert
    with check (auth.uid() = id);

drop policy if exists "users can update their own profile" on public.profiles;
create policy "users can update their own profile"
    on public.profiles for update
    using (auth.uid() = id);

-- ============ products ============
-- Mirrors the "Product" model.
create table if not exists public.products (
    id uuid primary key default gen_random_uuid(),
    category text,
    name text,
    "basePrice" numeric,
    specs text,
    type text,
    color text,
    weight text
);

alter table public.products enable row level security;

drop policy if exists "products are publicly readable" on public.products;
create policy "products are publicly readable"
    on public.products for select
    using (true);

-- Only rows where the signed-in user's own profile has role = 'admin' may write.
-- (The Android app currently lets any user set their own role via Manage Profile,
-- as a grading/demo convenience - but this check still stops writes from anyone
-- who hasn't explicitly done that, including anonymous requests using only the
-- public anon key.)
drop policy if exists "admins can modify products" on public.products;
create policy "admins can modify products"
    on public.products for all
    to authenticated
    using (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'))
    with check (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'));

-- ============ print_orders ============
-- Mirrors the "PrintOrder" model. Order items are embedded as jsonb (matches the
-- original Firestore document shape), so no separate order_items table/join is needed.
create table if not exists public.print_orders (
    id uuid primary key default gen_random_uuid(),
    name text,
    type text,
    "totalAmount" numeric,
    status text,
    "userId" uuid references auth.users (id) on delete cascade,
    "deliveryId" text,
    "orderItems" jsonb,
    "createdAt" timestamptz not null default now()
);

-- These columns were added after the table already existed for some projects - same
-- "add column if not exists" reasoning as profiles.role above.
alter table public.print_orders add column if not exists "paperType" text;
alter table public.print_orders add column if not exists "size" text;
alter table public.print_orders add column if not exists "customText" text;
alter table public.print_orders add column if not exists "designUrl" text;
alter table public.print_orders add column if not exists "pickupTime" text;

alter table public.print_orders enable row level security;

-- A user can read their own orders; an admin can read everyone's.
drop policy if exists "users can read their own orders" on public.print_orders;
create policy "users can read their own orders"
    on public.print_orders for select
    using (
        auth.uid() = "userId"
        or exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin')
    );

drop policy if exists "users can create their own orders" on public.print_orders;
create policy "users can create their own orders"
    on public.print_orders for insert
    with check (auth.uid() = "userId");

drop policy if exists "users can delete their own pending orders" on public.print_orders;
create policy "users can delete their own pending orders"
    on public.print_orders for delete
    using (auth.uid() = "userId" and status = 'PENDING');

-- A user may only update (e.g. reschedule) their own order while it's still pending;
-- an admin may update any order (e.g. advance its status).
drop policy if exists "users can update their own pending orders" on public.print_orders;
create policy "users can update their own pending orders"
    on public.print_orders for update
    using (auth.uid() = "userId" and status = 'PENDING')
    with check (auth.uid() = "userId");

drop policy if exists "admins can update any order" on public.print_orders;
create policy "admins can update any order"
    on public.print_orders for update
    to authenticated
    using (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'))
    with check (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'));

-- ============ saved_designs ============
create table if not exists public.saved_designs (
    id uuid primary key default gen_random_uuid(),
    "userId" uuid references auth.users (id) on delete cascade,
    name text,
    "customText" text,
    "designUrl" text,
    "createdAt" timestamptz not null default now()
);

alter table public.saved_designs enable row level security;

drop policy if exists "users can read own saved designs" on public.saved_designs;
create policy "users can read own saved designs"
    on public.saved_designs for select
    using (auth.uid() = "userId");

drop policy if exists "users can insert own saved designs" on public.saved_designs;
create policy "users can insert own saved designs"
    on public.saved_designs for insert
    with check (auth.uid() = "userId");

drop policy if exists "users can delete own saved designs" on public.saved_designs;
create policy "users can delete own saved designs"
    on public.saved_designs for delete
    using (auth.uid() = "userId");

-- ============ samples ============
create table if not exists public.samples (
    id uuid primary key default gen_random_uuid(),
    title text,
    "imageUrl" text,
    category text,
    "bleedMargins" text,
    "colorFormats" text,
    "templateUrl" text
);

alter table public.samples enable row level security;

drop policy if exists "samples are publicly readable" on public.samples;
create policy "samples are publicly readable"
    on public.samples for select
    using (true);

drop policy if exists "admins can modify samples" on public.samples;
create policy "admins can modify samples"
    on public.samples for all
    to authenticated
    using (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'))
    with check (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'));

-- ============ promotions ============
create table if not exists public.promotions (
    id uuid primary key default gen_random_uuid(),
    title text,
    description text,
    code text unique,
    discount numeric,
    "expiryDate" text
);

alter table public.promotions enable row level security;

drop policy if exists "promotions are publicly readable" on public.promotions;
create policy "promotions are publicly readable"
    on public.promotions for select
    using (true);

drop policy if exists "admins can modify promotions" on public.promotions;
create policy "admins can modify promotions"
    on public.promotions for all
    to authenticated
    using (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'))
    with check (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'));

-- ============ resources ============
-- category: e.g. 'faq', 'support', 'guideline'
create table if not exists public.resources (
    id uuid primary key default gen_random_uuid(),
    title text,
    content text,
    category text
);

alter table public.resources enable row level security;

drop policy if exists "resources are publicly readable" on public.resources;
create policy "resources are publicly readable"
    on public.resources for select
    using (true);

drop policy if exists "admins can modify resources" on public.resources;
create policy "admins can modify resources"
    on public.resources for all
    to authenticated
    using (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'))
    with check (exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin'));

-- ============ seed data ============
-- Same 4 sample products the old Spring Boot DataSeeder created.
insert into public.products (category, name, "basePrice", specs, type, color, weight)
select * from (values
    ('Business Cards', 'Standard Business Cards', 15.99, '3.5" x 2", 300gsm matte, box of 250', 'card', 'full-color', '300gsm'),
    ('Flyers', 'A5 Flyers', 29.99, 'A5, 150gsm gloss, pack of 100', 'flyer', 'full-color', '150gsm'),
    ('Posters', 'A2 Poster', 9.99, 'A2, 200gsm satin', 'poster', 'full-color', '200gsm'),
    ('Banners', 'Vinyl Banner 3x6ft', 49.99, '3ft x 6ft, weatherproof vinyl, grommets included', 'banner', 'full-color', '13oz vinyl')
) as seed(category, name, "basePrice", specs, type, color, weight)
where not exists (select 1 from public.products);

-- Seed samples gallery
insert into public.samples (title, "imageUrl", category, "bleedMargins", "colorFormats", "templateUrl")
select * from (values
    ('Modern Business Card Template', 'https://images.unsplash.com/photo-1589254065878-42c9da997008?w=200', 'Business Cards', '0.125 inches (3.175mm)', 'CMYK color mode, 300 DPI minimum', 'https://example.com/templates/card1.pdf'),
    ('Corporate Flyer Template', 'https://images.unsplash.com/photo-1626785774573-4b799315345d?w=200', 'Flyers', '0.25 inches (6.35mm)', 'CMYK, 300 DPI', 'https://example.com/templates/flyer1.pdf'),
    ('Event Promotion Poster Template', 'https://images.unsplash.com/photo-1561070791-26c113006238?w=200', 'Posters', '0.25 inches', 'CMYK / RGB accepted, 150+ DPI', 'https://example.com/templates/poster1.pdf')
) as seed(title, "imageUrl", category, "bleedMargins", "colorFormats", "templateUrl")
where not exists (select 1 from public.samples);

-- Seed promotions
insert into public.promotions (title, description, code, discount, "expiryDate")
select * from (values
    ('Welcome Promo', 'Get 10% off your first printing order!', 'WELCOME10', 10.0, '2026-12-31'),
    ('Festive Season Sale', 'Special holiday discount of 20% on all banners and poster prints.', 'FESTIVE20', 20.0, '2026-08-31')
) as seed(title, description, code, discount, "expiryDate")
where not exists (select 1 from public.promotions);

-- Seed resources (FAQs & Guidelines)
insert into public.resources (title, content, category)
select * from (values
    ('What file formats do you accept?', 'We accept PDF, PNG, JPG, and AI files. For vector layouts and crisp text, PDF is highly recommended.', 'guideline'),
    ('What are bleed margins?', 'Bleed margins are the outer edges of your design that get trimmed off after printing. Always extend background colors or artwork 0.125 inches beyond the crop marks to prevent thin white edges.', 'guideline'),
    ('How do I contact customer support?', 'You can email us at support@printxpress.com or call our hotline: +94 11 234 5678. We are open Mon-Fri, 9AM to 5PM.', 'support'),
    ('Can I cancel my print order?', 'Yes, you can cancel any order from the My Orders screen as long as its status is PENDING (before printing has started). Once the status changes to PROCESSING, cancellation is no longer possible.', 'faq')
) as seed(title, content, category)
where not exists (select 1 from public.resources);
