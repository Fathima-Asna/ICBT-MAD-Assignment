-- PrintXpress Supabase setup
-- Run this once in the Supabase dashboard: SQL Editor -> New query -> paste -> Run.
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

alter table public.profiles enable row level security;

-- Anyone (including anonymous/pre-login requests) can read profiles - needed so
-- username-based login can look up the matching email before the user is authenticated.
create policy "profiles are publicly readable"
    on public.profiles for select
    using (true);

-- A user can only create/update their own profile row.
create policy "users can insert their own profile"
    on public.profiles for insert
    with check (auth.uid() = id);

create policy "users can update their own profile"
    on public.profiles for update
    using (auth.uid() = id);

-- ============ products ============
-- Mirrors the "Product" model. Read-only from the app; curate rows via this SQL editor.
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

create policy "products are publicly readable"
    on public.products for select
    using (true);

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

alter table public.print_orders enable row level security;

create policy "users can read their own orders"
    on public.print_orders for select
    using (auth.uid() = "userId");

create policy "users can create their own orders"
    on public.print_orders for insert
    with check (auth.uid() = "userId");

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
