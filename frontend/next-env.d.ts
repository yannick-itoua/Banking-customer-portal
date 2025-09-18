/** @type {import('next').NextConfig} */
declare const _default: {
    experimental: {
        appDir: boolean;
    };
    rewrites(): Promise<{
        source: string;
        destination: string;
    }[]>;
};
export = _default;